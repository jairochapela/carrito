package cursojava.carrito.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cursojava.carrito.models.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    /**
     * Consulta para obtener el pedido en fase de elaboración (el status es nulo todavía)
     * para el usuario indicado.
     * @param userId ID del usuario para el que se quiere obtener el pedido en proceso
     * @return El pedido en proceso del usuario, o null si no existe ningún pedido en proceso para ese usuario
     */
    @Query("select ord from Order ord where ord.user.id = ?1 and ord.status is null limit 1")
    Order findCurrentOrderByUser(Integer userId);
}
