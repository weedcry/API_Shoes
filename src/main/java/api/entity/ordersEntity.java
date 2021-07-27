package api.entity;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name="orders")
public class ordersEntity {
    public enum Status {
        DELIVERING,DELIVERED,UNCONFIRM,CANCEL;
    }


    @Id
    private String id;

    @NotNull
    private float total;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @NotBlank
    private String address;

    @NotBlank
    private String phone;

    @Email
    private String email;

    @NotBlank
    private String fullname;

    private String createdBy;

    private Date createdDate;

    private String modifiedBy;

    private Date modifiedDate;

    @ManyToOne
    @JoinColumn(name ="customer_id")
    private customersEntity customersEntity;

    @ManyToOne
    @JoinColumn(name="payment_id")
    private paymentEntity paymentEntity;

    @OneToMany(mappedBy = "ordersEntity", cascade = CascadeType.ALL)
    private Collection<orderdetailEntity> orderdetailEntities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public api.entity.customersEntity getCustomersEntity() {
        return customersEntity;
    }

    public void setCustomersEntity(api.entity.customersEntity customersEntity) {
        this.customersEntity = customersEntity;
    }

    public api.entity.paymentEntity getPaymentEntity() {
        return paymentEntity;
    }

    public void setPaymentEntity(api.entity.paymentEntity paymentEntity) {
        this.paymentEntity = paymentEntity;
    }

    public Collection<orderdetailEntity> getOrderdetailEntities() {
        return orderdetailEntities;
    }

    public void setOrderdetailEntities(Collection<orderdetailEntity> orderdetailEntities) {
        this.orderdetailEntities = orderdetailEntities;
    }
}
