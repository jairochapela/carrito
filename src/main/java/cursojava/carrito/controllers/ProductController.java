package cursojava.carrito.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import cursojava.carrito.models.Product;
import cursojava.carrito.repositories.ProductRepository;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public String listProducts(Model modelUI) {
        List<Product> products = productRepository.findAll();
        modelUI.addAttribute("products", products);
        return "product_list";
    }

    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable Integer id, Model modelUI) {
        Product product = productRepository.findById(id).orElseThrow();
        modelUI.addAttribute("product", product);
        return "product_details";
    }

    @PostMapping("/products/{id}")
    public String addToCart(@PathVariable Integer id, Integer quantity) {
        // Lógica para agregar el producto al carrito
        return "redirect:/products"; //TODO: redirigir a la página del carrito, no a la lista de productos
    }
}