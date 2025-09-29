package app.dao;

import app.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

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
            
        }

    }

}