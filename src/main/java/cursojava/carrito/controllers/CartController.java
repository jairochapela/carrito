package cursojava.carrito.controllers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cursojava.carrito.models.Order;
import cursojava.carrito.models.OrderProduct;
import cursojava.carrito.models.Product;
import cursojava.carrito.models.User;
import cursojava.carrito.repositories.OrderProductRepository;
import cursojava.carrito.repositories.OrderRepository;
import cursojava.carrito.repositories.ProductRepository;
import cursojava.carrito.repositories.UserRepository;

@Controller
public class CartController {
    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    
    @GetMapping("/cart")
    public String viewCart(Model modelUI) {
        // Identificar al usuario actual a partir del contexto de seguridad.
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String emailUsuarioLogueado = authentication.getName();
        User usuarioActual = userRepository.findByEmail(emailUsuarioLogueado);

        // Obtener de la base de datos el pedido sin cerrar asociado al 
        // usuario actual y sus líneas de pedido.
        Order pedidoEnCurso = orderRepository.findCurrentOrderByUser(usuarioActual.getId());
        if (pedidoEnCurso == null) {
            // Si no existe un pedido en curso para el usuario, crear uno nuevo vacío.
            pedidoEnCurso = new Order();
            pedidoEnCurso.setUser(usuarioActual);
            pedidoEnCurso.setStatus(null);
            pedidoEnCurso.setCreatedAt(new Date());
            orderRepository.save(pedidoEnCurso);
        }

        // Solicita a la base de datos las líneas de pedido que pertenecen al pedido en curso.
        List<OrderProduct> lineasPedido = orderProductRepository.findByOrderId(pedidoEnCurso.getId());

        // Cargamos los datos obtenidos en el modelo de la vista para que aparezcan en la plantilla. 
        modelUI.addAttribute("order", pedidoEnCurso);
        modelUI.addAttribute("lines", lineasPedido);

        // Calculamos el total del pedido sumando el precio unitario por la cantidad de cada línea de pedido.
        BigDecimal total = lineasPedido.stream()
            .map(linea -> linea.getUnitPrice().multiply(BigDecimal.valueOf(linea.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        modelUI.addAttribute("total", total);        

        // Entregamos la plantilla "cart.html".
        return "cart";
    }


    /**
     * Finaliza la compra del carrito de compras del usuario actual.
     * @return
     */
    @PostMapping("/cart/checkout")
    @Transactional(rollbackFor = { SQLException.class })
    public String checkout() {

        // Identificar al usuario actual a partir del contexto de seguridad.
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String emailUsuarioLogueado = authentication.getName();
        User usuarioActual = userRepository.findByEmail(emailUsuarioLogueado);

        // Obtener el carrito de compras del usuario que todavía no ha sido finalizado (estado null).
        Order pedidoEnCurso = orderRepository.findCurrentOrderByUser(usuarioActual.getId());
        if (pedidoEnCurso == null) {
            return "redirect:/cart";
        }

        // Líneas de pedido del carrito de compras del usuario.
        List<OrderProduct> lineasPedido = orderProductRepository.findByOrderId(pedidoEnCurso.getId());

        // Para calcular el total del pedido.
        BigDecimal total = BigDecimal.ZERO;



        // Para cada una de las líneas de pedido:
        for (OrderProduct linea : lineasPedido) {
            // Obtener el producto referido en dicha línea.
            Product producto = linea.getProduct();
            // Decrementar stock.
            producto.setStock(producto.getStock() - linea.getQuantity());
            productRepository.save(producto);

            // Calcular el subtotal (cantidad * precio_unitario) y añadirlo al total del pedido.
            BigDecimal quantitybd = BigDecimal.valueOf(linea.getQuantity());
            total = total.add(linea.getUnitPrice().multiply(quantitybd));
        }

        // Establecer el estado del pedido a 'F' (Finalizado).
        pedidoEnCurso.setStatus('F');
        // Guardar el pedido actualizado en la base de datos.
        orderRepository.save(pedidoEnCurso);


   
        // Redirigir al usuario a una página de pagos (simulada).
        return "redirect:/cart/payment"; // Redirigir a la página de pagos (simulada).
    }


    @GetMapping("/cart/payment")
    public String payment() {
        return "payment";
    }


    @PostMapping("/cart/payment")
    public String paymentSelected(@RequestParam("metodo_pago") String metodoPago) {
        System.out.println("El método de pago seleccionado es: " + metodoPago);
        return "payment";
    }    
}
