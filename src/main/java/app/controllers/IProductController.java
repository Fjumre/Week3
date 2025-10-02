package app.controllers;

import java.util.logging.Handler;

public interface IProductController {

    Handler createProduct();
    Handler updateProduct();
    Handler deleteProduct();
    Handler deleteProductByName();
    Handler getProduct();
    Handler getProductByName();
    Handler getAllProducts();
}
