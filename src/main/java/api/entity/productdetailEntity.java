package api.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Table(name = "productdetail")
public class productdetailEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="productid")
    private productsEntity productsEntity;

//    @JoinColumns({
//            @JoinColumn(name = "id", referencedColumnName = "id"),
//            @JoinColumn(name = "color",referencedColumnName = "color")
//    })

    @NotNull
    @Min(35)
    private int size;

    @OneToMany(mappedBy = "productdetail")
    private Collection<repositoryEntity> repositoryEntities;


    @OneToMany(mappedBy = "productdetail")
    private Collection<shopcartEntity> shopcartproduct;


    @OneToMany(mappedBy = "productdetailEntity",cascade = CascadeType.ALL)
    private Collection<orderdetailEntity> orderdetailEntities;


    public api.entity.productsEntity getProductsEntity() {
        return productsEntity;
    }

    public void setProductsEntity(api.entity.productsEntity productsEntity) {
        this.productsEntity = productsEntity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
