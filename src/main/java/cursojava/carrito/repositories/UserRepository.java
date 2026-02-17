package cursojava.carrito.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cursojava.carrito.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    // No hace falta implementar nada para las consultas básicas.

    /**
     * Definimos una consulta personalizada para buscar un usuario por su dirección de correo electrónico.
     * @param emailAddress La dirección de correo electrónico del usuario.
     * @return El usuario que coincide con la dirección de correo electrónico proporcionada.
     */
    @Query("select u from User u where u.email = ?1")
    User findByEmail(String email);
}
