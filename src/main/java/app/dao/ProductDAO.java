package app.dao;

import app.model.Product;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.*;

import java.util.List;

//DAO are used for CRUD/Database
public class ProductDAO {

    private final EntityManagerFactory emf;

    public ProductDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @RolesAllowed("admin")
    public Product createProduct(Product product) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(product);
            tx.commit();
            return product;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public Product getProduct(int pId) {
        EntityManager em = emf.createEntityManager();
        try {
            // Read-only ops don't strictly need a transaction
            return em.find(Product.class, pId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public Product getProductByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Product> q = em.createQuery(
                    "SELECT p FROM Product p WHERE p.name = :name", Product.class);
            q.setParameter("name", name);
            return q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public List<Product> getAllProducts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @RolesAllowed("admin")
    public void updateProduct(Product product) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(product);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @RolesAllowed("admin")
    public boolean deleteProduct(int pId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product product = em.find(Product.class, pId);
            if (product == null) {
                tx.rollback();
                return false;
            }
            em.remove(product);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @RolesAllowed("admin")
    public boolean deleteProductByName(String name) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product p = em.createQuery(
                            "SELECT p FROM Product p WHERE p.name = :name", Product.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (p == null) {
                tx.rollback();
                return false;
            }
            em.remove(p);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}
