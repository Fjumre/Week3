package app.controllers;


import io.javalin.http.Handler;
public interface IProductController {

    Handler createProduct();
    Handler updateProduct();
    Handler deleteProduct();
    Handler deleteProductByName();
    Handler getProduct();
    Handler getProductByName();
    Handler getAllProducts();
}
