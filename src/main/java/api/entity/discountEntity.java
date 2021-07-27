package api.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Table(name = "discount")
public class discountEntity {
    @Id
    private String id;

    @NotNull
    private float percent;

    @OneToMany(mappedBy = "discountEntitys")
    private Collection<productsEntity> productsEntities;

    public discountEntity(){}

    public discountEntity(String id, float percent, Collection<productsEntity> productsEntities) {
        this.id = id;
        this.percent = percent;
        this.productsEntities = productsEntities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

}
