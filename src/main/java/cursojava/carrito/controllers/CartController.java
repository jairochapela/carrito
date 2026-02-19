package cursojava.carrito.controllers;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import cursojava.carrito.models.Order;
import cursojava.carrito.models.OrderProduct;
import cursojava.carrito.models.User;
import cursojava.carrito.repositories.OrderProductRepository;
import cursojava.carrito.repositories.OrderRepository;
import cursojava.carrito.repositories.UserRepository;

@Controller
public class CartController {
    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;
    
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
}
