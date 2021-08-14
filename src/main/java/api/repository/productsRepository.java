package api.repository;

import api.entity.categoryEntity;
import api.entity.productsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface productsRepository extends JpaRepository<productsEntity,Long > {

    @Query(value="select * from products where status = 'ACTIVE'",nativeQuery = true)
    List<productsEntity> getlistproductactive();

    productsEntity findById(long id);

    Page<productsEntity> findByCategory_id(Long id, Pageable pageable);

//    @Query("select p from productsEntity p where p.name like %?1%")
    Page<productsEntity> findByNameContaining(String name,Pageable pageable);
}
