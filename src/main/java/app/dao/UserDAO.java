package app.dao;

import app.exceptions.EntityNotFoundException;
import app.model.Role;
import app.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Set;

public class UserDAO implements ISecurityDAO {
    private EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public User createUser(String username, String password, String email, String fullName, Integer phone){

        EntityManager em= emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();

        try{
            tx.begin();
            User user=new User(username,password,email,fullName,phone);

            Role userRole= em.createQuery("SELECT r from Role r WHERE r.roleName=:roleName",Role.class)
                    .setParameter("roleName", "user")
                    .getResultStream()
                    .findFirst()
                    .orElseGet(()->{
                        Role newRole= new Role("user");
                        em.persist(newRole);
                        return newRole;
                    });

        user.addRole(userRole);
        em.persist(user);
        tx.commit();
        return user;
    } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }

    }

    @Override
    public Role createRole(String role) {
        return null;
    }


    @Override
    public User update(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        return user;
    }

    @Override
    public User UpdateUser(String username, String password, Set<Role> roles) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = new User(username, password, roles);
        Role userRole = em.find(Role.class, "user");
        if (userRole == null) {
            userRole = new Role("user");
            em.persist(userRole);
        }
        user.addRole(userRole);
        em.merge(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }


    @Override
    public User getUserById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }



    @Override
    public User verifyUserForReset(String email, String password) throws EntityNotFoundException {
        return null;
    }

    @Override
    public User UpdatePassword(User user, String newPassword) {
        return null;
    }
    public User verifyUser(String username, String password) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            // Using JPQL to query by username
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();


            if (!user.verifyUser(password)) {
                throw new EntityNotFoundException("Wrong password");
            }
            return user;
        } catch (NoResultException e) {
            throw new EntityNotFoundException("No user found with that username: " + username);
        } finally {
            em.close();
        }
    }
    public User findByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class)
                    .setParameter("u", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
    public User addRoleToUser(String username, String roleName) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class)
                    .setParameter("u", username)
                    .getSingleResult();

            Role role = em.find(Role.class, roleName); // PK is roleName
            if (role == null) {
                role = new Role(roleName);
                em.persist(role);
            }

            user.addRole(role); // links both sides
            em.merge(user);     // persist join row
            em.getTransaction().commit();
            return user;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Failed to add role: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    @Override
    public List<User> getAlleUser() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteUser(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User u = em.find(User.class, id);
            if (u == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(u);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Delete failed: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}