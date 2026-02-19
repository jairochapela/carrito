package cursojava.carrito.models;

public class OPKey {
    private Integer order;
    private Integer product;

    public OPKey() {}

    public OPKey(Integer order, Integer product) {
        this.order = order;
        this.product = product;
    }

    // Getters y setters
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    //TODO: falta el método equals() y hashCode()
}
