package app.endpoints;

import jakarta.persistence.EntityManagerFactory;
import app.config.HibernateConfig;


import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class Endpoints {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
}
