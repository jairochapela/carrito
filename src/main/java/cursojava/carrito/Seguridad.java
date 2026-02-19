package cursojava.carrito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import cursojava.carrito.models.User;
import cursojava.carrito.repositories.UserRepository;

/**
 * Clase de configuración de seguridad para la aplicación. 
 * Aquí se pueden definir las reglas de acceso, autenticación y autorización. 
 * 
 * El control de acceso estará basado en una consulta a la base de datos, donde se
 * comprobará si el email y el password proporcionados coinciden con los almacenados en 
 * la tabla de usuarios (entidad User).
 */
@Configuration
@EnableWebSecurity
public class Seguridad {
    
    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/products", "/products/*").permitAll()
            .requestMatchers("/cart").hasAuthority("USER")
        )
        .formLogin(form -> form
            .loginPage("/login")    // Página de login personalizada en ruta /login
            .defaultSuccessUrl("/", true)  // Redirigir al inicio después del login exitoso
            .permitAll()  // Permitir acceso a la página de login sin autenticación   
        )
        .logout(logout -> logout.permitAll()); // Permitir a todos los usuarios cerrar sesión       
        return http.build();
    }

    /**
     * Configura el codificador de contraseñas utilizando BCrypt, para que las
     * contraseñas de la aplicación estén cifradas en el almacenamiento.
     */
    @Bean
	PasswordEncoder passwordEncoder() {
		//return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance(); //TODO: cambiar a BCryptPasswordEncoder para cifrar las contraseñas, no almacenarlas en texto plano
	}



    /**
     * Define los detalles de usuarios que pueden acceder a la aplicación.
     * Localiza un usuario en la base de datos por su email y devuelve un objeto
     * de la clase org.springframework.security.core.userdetails.User con sus credenciales y roles.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetailsService userDetailsService = (email) -> {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("Usuario no encontrado");
            }
            // Devolvemos un User de Spring Security (no confundir con nuestra entidad User),
            // que contiene las credenciales y roles del usuarios autenticado.
            return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(user.getPassword())
                .authorities("USER")
                .build();
        };
        return userDetailsService;
    }

}
