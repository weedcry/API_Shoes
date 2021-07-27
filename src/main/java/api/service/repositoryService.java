package api.service;

import api.DTO.repositoryDTO;
import api.entity.productdetailEntity;
import api.entity.repositoryEntity;
import api.entity.typeEntity;
import api.repository.productdetailRepository;
import api.repository.productsRepository;
import api.repository.repositoryRepository;
import api.repository.typeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
public class repositoryService {
    @Autowired
    repositoryRepository repositoryRepository;

    @Autowired
    productsRepository productsRepository;

    @Autowired
    productdetailRepository productdetailRepository;

    @Autowired
    typeRepository typeRepository;

    @Autowired
    ModelMapper modelMapper;



    public List getListRepository(){
        List<repositoryEntity> list = repositoryRepository.findAll();
        return list;
    }



    // tạo phiếu nhập (PN)
    public repositoryEntity createRepository(repositoryDTO repository){
        repositoryEntity repositoryEntity = null;
       productdetailEntity productdetail =  productdetailRepository.findById(repository.getProductdetailid());
        typeEntity type = typeRepository.findById(repository.getTypeid());
        if(productdetail == null || type == null){
            return  repositoryEntity;
        }
        String id = "PN"+ repository.getDatecreated().getTime();

        repositoryEntity = modelMapper.map(repository, api.entity.repositoryEntity.class);
        repositoryEntity.setId(id);
        repositoryEntity.setProductdetail(productdetail);
        repositoryEntity.setType(type);
        repositoryEntity.setDatecreated(repository.getDatecreated());
        repositoryEntity.setPrice(repository.getPrice());
        repositoryEntity.setQuantity(repository.getQuantity());
        repositoryRepository.save(repositoryEntity);
        return repositoryEntity;
    }




}
