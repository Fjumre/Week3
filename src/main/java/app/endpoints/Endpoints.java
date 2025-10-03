package app.endpoints;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;

import app.controllers.*;
import app.dao.ProductDAO;
import app.dao.UserDAO;
import app.security.RouteRoles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Endpoints {

    // Controllers & DAOs
    private static final ISecurityController securityController = new SecurityController();

    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    private static final UserDAO userDAO = new UserDAO(emf);
    private static final IUserController userController = new UserController(userDAO);

    private static final ProductDAO productDAO = new ProductDAO(emf);
    private static final IProductController productController = new ProductController(productDAO);

    private static final ObjectMapper om = new ObjectMapper();

    public static void startServer(int port) {
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        applicationConfig
                .initiateServer()
                .startServer(port)
                .setExceptionHandling()
                .setupAccessManager()
                .configureCors()
                // Route groups
                .setRoute(getSecurityRoutes())
                .setRoute(getSecuredRoutes())
                .setRoute(Endpoints::getUserRoutes)
                .setRoute(Endpoints::getProductRoutes)
                .checkSecurityRoles();
    }

    //Public auth endpoints (no auth required)
    public static EndpointGroup getSecurityRoutes() {
        return () -> {
            path("/auth", () -> {
                post("/login", securityController.login(), RouteRoles.ANYONE);
                post("/register", securityController.register(), RouteRoles.ANYONE);
                post("/resetpassword", securityController.resetOfPassword(), RouteRoles.ANYONE);
            });
        };
    }

    //Simple protected test endpoints
    public static EndpointGroup getSecuredRoutes() {
        return () -> {
            path("/protected", () -> {
                before(securityController.authenticate());
                get("/user",
                        ctx -> ctx.json(om.createObjectNode().put("msg", "Hello from USER Protected")),
                        RouteRoles.USER);
                get("/admin",
                        ctx -> ctx.json(om.createObjectNode().put("msg", "Hello from ADMIN Protected")),
                        RouteRoles.ADMIN);
            });
        };
    }

    // User routes (require auth as per roles)
    public static void getUserRoutes() {
        path("/user", () -> {
            before(securityController.authenticate());

            // Collection & creation
            get("/all", userController.getAllUsers(), RouteRoles.ADMIN);
            post("/create", userController.createUser(), RouteRoles.ADMIN);

            // By ID
            get("/{id}", userController.getUserById(), RouteRoles.USER, RouteRoles.ADMIN);
            put("/update/{id}", userController.updateUser(), RouteRoles.USER, RouteRoles.ADMIN);
            delete("/delete/{id}", userController.deleteUser(), RouteRoles.USER, RouteRoles.ADMIN);

            // Username-based endpoints (uncomment if using)
            // get("/u/{username}", userController.getUserByUsername(), RouteRoles.USER, RouteRoles.ADMIN);
            // put("/u/{username}", userController.updateUserByUsername(), RouteRoles.USER, RouteRoles.ADMIN);

            post("/logout", userController.logout(), RouteRoles.USER, RouteRoles.ADMIN);

            get("/error", ctx -> {
                throw new Exception("Test error from /user/error");
            });
        });
    }

    // Product routes
    public static void getProductRoutes() {
        path("/products", () -> {
            // Public read endpoints
            get("/", productController.getAllProducts(), RouteRoles.ANYONE);
            get("/{id}", productController.getProduct(), RouteRoles.ANYONE);
            get("/name/{name}", productController.getProductByName(), RouteRoles.ANYONE);

            // Mutating endpoints (admin only)
            post("/", productController.createProduct(), RouteRoles.ADMIN);
            put("/", productController.updateProduct(), RouteRoles.ADMIN);
            delete("/{id}", productController.deleteProduct(), RouteRoles.ADMIN);
            delete("/name/{name}", productController.deleteProductByName(), RouteRoles.ADMIN);
        });
    }
}
