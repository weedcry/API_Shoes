package api.repository;

import api.entity.ordersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ordersRepository extends JpaRepository<ordersEntity, String> {

    ordersEntity findById(String id);

    @Query(value="select * from orders where customer_id = ?1 ",nativeQuery = true)
    List<ordersEntity> findListOrderCustomer(long customerid);


}