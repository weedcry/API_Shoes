package api.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="orderdetail")
public class orderdetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="productdetailid")
    private productdetailEntity productdetailEntity;

    @NotNull
    @Min(1)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="order_id")
    private ordersEntity ordersEntity;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public api.entity.productdetailEntity getProductdetailEntity() {
        return productdetailEntity;
    }

    public void setProductdetailEntity(api.entity.productdetailEntity productdetailEntity) {
        this.productdetailEntity = productdetailEntity;
    }

    public api.entity.ordersEntity getOrdersEntity() {
        return ordersEntity;
    }

    public void setOrdersEntity(api.entity.ordersEntity ordersEntity) {
        this.ordersEntity = ordersEntity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
