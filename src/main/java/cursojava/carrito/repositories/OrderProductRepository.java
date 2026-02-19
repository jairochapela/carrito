package cursojava.carrito.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cursojava.carrito.models.OPKey;
import cursojava.carrito.models.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OPKey> {
    
    /**
     * Busca una relación OrderProduct por el ID de la orden y el ID del producto.
     * @param orderId ID del pedido
     * @param productId ID del producto
     * @return La relación encontrada o null si no existe
     */
    @Query("select op from OrderProduct op where op.order.id = ?1 and op.product.id = ?2")
    OrderProduct findByOrderIdAndProductId(Integer orderId, Integer productId);

    /**
     * Obtiene una lista de líneas de pedido (OrderProduct) asociadas a un pedido específico,
     * identificado por su ID.
     * @param id ID del pedido para el cual se desean obtener las líneas de pedido
     * @return Lista de líneas de pedido asociadas al pedido especificado
     */
    @Query("select op from OrderProduct op join fetch op.product where op.order.id = ?1")
    List<OrderProduct> findByOrderId(Integer id);
}
