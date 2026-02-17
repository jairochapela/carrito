package cursojava.carrito.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import cursojava.carrito.models.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    
}
