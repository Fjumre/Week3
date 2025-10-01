package app.dao;

import app.exceptions.EntityNotFoundException;
import app.model.Role;
import app.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

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
    public User addRoleToUser(String username, String roleName) {
        EntityManager em = emf.createEntityManager();

        User user;
        try {
            em.getTransaction().begin();
            user = em.find(User.class, username);
            Role role = em.find(Role.class, roleName);

            user.addRole(role); // Modify the collection in the managed entity

            em.merge(user); // Ensure changes are cascaded to the database

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to add role to user due to: " + e.getMessage(), e);
        }

        return user;
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
    public List<User> getAlleUser() {
        return List.of();
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
    public void deleteUser(int id) {

    }

    @Override
    public User verifyUserForReset(String email, String password) throws EntityNotFoundException {
        return null;
    }

    @Override
    public User UpdatePassword(User user, String newPassword) {
        return null;
    }

}