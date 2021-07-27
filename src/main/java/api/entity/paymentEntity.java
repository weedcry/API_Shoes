package api.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name="payment")
public class paymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String url;

    @OneToMany(mappedBy = "paymentEntity")
    private Collection<ordersEntity> ordersEntities;

    public paymentEntity(){}

    public paymentEntity(Long id, String name, String url, Collection<ordersEntity> ordersEntities) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.ordersEntities = ordersEntities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Collection<ordersEntity> getOrdersEntities() {
        return ordersEntities;
    }

    public void setOrdersEntities(Collection<ordersEntity> ordersEntities) {
        this.ordersEntities = ordersEntities;
    }
}
