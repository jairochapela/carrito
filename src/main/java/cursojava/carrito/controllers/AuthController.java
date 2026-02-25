package cursojava.carrito.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * Gestión de la autenticación y autorización de usuarios mediante el formulario de login y
 * el botón de logout.
 */
@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

}
