package cursojava.carrito.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import cursojava.carrito.models.Order;
import cursojava.carrito.models.OrderProduct;
import cursojava.carrito.models.Product;
import cursojava.carrito.models.User;
import cursojava.carrito.repositories.OrderProductRepository;
import cursojava.carrito.repositories.OrderRepository;
import cursojava.carrito.repositories.ProductRepository;
import cursojava.carrito.repositories.UserRepository;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;


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


    /**
     * Agrega un producto al carrito de compras.
     * @param id ID del producto a agregar al carrito
     * @param quantity Cantidad del producto a agregar al carrito
     * @return Redirige a la página del carrito después de agregar el producto
     */
    @PostMapping("/products/{id}")
    public String addToCart(@PathVariable Integer id, Integer quantity) {

        // Averiguar el usuario actual del user details service de Spring Security, para asociar el carrito a ese usuario
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String emailUsuarioLogueado = authentication.getName();
        User usuarioActual = userRepository.findByEmail(emailUsuarioLogueado);

        // Si para el usuario actual no existe pedido en proceso (status nulo):
        //     Crear un nuevo pedido en estado "en proceso" (nulo) para el usuario actual
        // Si no:
        //     Obtener el pedido en proceso del usuario actual
        Order pedidoActual = orderRepository.findCurrentOrderByUser(usuarioActual.getId());
        if (pedidoActual == null) {
            pedidoActual = new Order();
            pedidoActual.setUser(usuarioActual);
            pedidoActual.setStatus(null);
            pedidoActual.setCreatedAt(new Date());
            orderRepository.save(pedidoActual);
        }

        // Con el pedido:
        //     Si el artículo ya existe en el pedido:
        //         Aumentar la cantidad del artículo en el pedido en tantas unicades como se hayan indicado
        //     Si no:
        //         Añadir el artículo al pedido con la cantidad indicada
        OrderProduct lineaPedido = orderProductRepository.findByOrderIdAndProductId(pedidoActual.getId(), id);
        if (lineaPedido == null) {
            lineaPedido = new OrderProduct();
            lineaPedido.setOrder(pedidoActual);
            lineaPedido.setProduct(productRepository.findById(id).orElseThrow());
            lineaPedido.setQuantity(quantity);
        } else {
            lineaPedido.setQuantity(lineaPedido.getQuantity() + quantity);
        }
        lineaPedido.setUnitPrice(lineaPedido.getProduct().getPrice());
        orderProductRepository.save(lineaPedido);

        // Redirigir a la página del carrito
        return "redirect:/cart";
    }
}