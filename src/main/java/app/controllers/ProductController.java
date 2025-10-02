package app.controllers;

import app.dao.ProductDAO;
import app.model.Product;
import io.javalin.http.Handler;

public class ProductController implements IProductController {

    private final ProductDAO dao;

    public ProductController(ProductDAO dao) {
        this.dao = dao;
    }

    @Override
    public Handler createProduct() {
        return ctx -> {
            Product incoming = ctx.bodyAsClass(Product.class);
            Product created = dao.createProduct(incoming);
            ctx.status(201).json(created);
        };
    }

    @Override
    public Handler updateProduct() {
        return ctx -> {
            Product incoming = ctx.bodyAsClass(Product.class);
            dao.updateProduct(incoming);
            ctx.status(204);
        };
    }

    @Override
    public Handler deleteProduct() {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean removed = dao.deleteProduct(id);
            if (removed) ctx.status(204);
            else ctx.status(404).result("Product not found");
        };
    }

    @Override
    public Handler deleteProductByName() {
        return ctx -> {
            String name = ctx.pathParam("name");
            boolean removed = dao.deleteProductByName(name);
            if (removed) ctx.status(204);
            else ctx.status(404).result("Product not found");
        };
    }

    @Override
    public Handler getProduct() {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Product p = dao.getProduct(id);
            if (p == null) ctx.status(404).result("Product not found");
            else ctx.json(p);
        };
    }

    @Override
    public Handler getProductByName() {
        return ctx -> {
            String name = ctx.pathParam("name");
            Product p = dao.getProductByName(name);
            if (p == null) ctx.status(404).result("Product not found");
            else ctx.json(p);
        };
    }

    @Override
    public Handler getAllProducts() {
        return ctx -> ctx.json(dao.getAllProducts());
    }
}
