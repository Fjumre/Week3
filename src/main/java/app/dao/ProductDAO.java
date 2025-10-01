package app.dao;

import app.model.Product;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ProductDAO {

    private EntityManagerFactory emf;

    public ProductDAO(EntityManagerFactory emf)
    {
        this.emf = emf;
    }

    @RolesAllowed("ADMIN")
    public Product createProduct(Product product){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();

        try {
            tx.begin();
            em.persist(product);
            tx.commit();
            return product;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
    }


    public  Product getProduct(int pId){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();
        try {
            tx.begin();
            Product product = em.find(Product.class, pId);
            tx.commit();
            return product;
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
    }

    public Product getProductByName(String name){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();
        try {
            tx.begin();
            Product productByName = em.find(Product.class, name);
            tx.commit();
            return productByName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
    }

    public List<Product> getAllProducts(){
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
    }


    @RolesAllowed("ADMIN")
    public  void updateProduct(Product product){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();
        try {
            tx.begin();
            em.merge(product);
            tx.commit();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
    }

    @RolesAllowed("ADMIN")
    public void deleteProduct(int pId){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();
        try {
            tx.begin();
            Product product = em.find(Product.class, pId);
            em.remove(product);
            tx.commit();

        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
    }

    @RolesAllowed("ADMIN")
    public void deleteProductByName(String name){
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();
        try {
            tx.begin();
            Product deleteProductByName = em.find(Product.class, name);
            em.remove(deleteProductByName);
            tx.commit();

        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
    }
}
