package api.service;

import api.DTO.imageDTO;
import api.DTO.productdetailDTO;
import api.DTO.productsDTO;
import api.entity.*;
import api.exception.IdNotFoundException;
import api.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Component
public class productsService {
    @Autowired
    productsRepository productsRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    categoryService categoryService;

    @Autowired
    discountRepository discountRepository;

    @Autowired
    productdetailRepository productdetailRepository;

    @Autowired
    imageRepository imageRepository;

    @Autowired
    discountService discountService;

    public productsDTO saveproducts(productsDTO productDTO){
        if(!categoryService.checkid(productDTO.getCategory())){
            throw new IdNotFoundException("could not find category "+productDTO.getCategory());
        }

        productsEntity productsEntitys = productsRepository.findById(productDTO.getId());
        if(productsEntitys == null){
            productsEntitys = modelMapper.map(productDTO,productsEntity.class);
        }else {
            // list image
            // list size
            productDTO.setColor(productDTO.getColor());
            productsEntitys.setPrice(productDTO.getPrice());
            productsEntitys.setModifiedby(productDTO.getModifiedby());
            productsEntitys.setModifieddate(productDTO.getModifieddate());
            productsEntitys.setDeadline(productDTO.getDeadline());
            productsEntitys.setDescription(productDTO.getDescription());
            productsEntitys.setDiscountEntitys(discountService.getDiscount(productDTO.getDiscount()));
            productsEntitys.setDeadline(productDTO.getDeadline());
            productsEntitys.setName(productDTO.getName());
            if(productsEntity.Status.ACTIVE.equals(productDTO.getStatus())){
                productsEntitys.setStatus(productsEntity.Status.ACTIVE);
            }else{
                productsEntitys.setStatus(productsEntity.Status.INACTIVE);
            }
            productsEntitys.setUnitype(productDTO.getUnitype());
            productsEntitys.setRating(productDTO.getRating());
        }
        final productsEntity finalproduct = productsEntitys;
        //image
        List<imageEntity> imageEntityList = productDTO.getListimage().stream().map(
                imageDTO -> {
                    imageEntity image = modelMapper.map(imageDTO,imageEntity.class);
                    image.setProductsEntity(finalproduct);
                    return image;
                }
        ).collect(Collectors.toList());

        // discount
         discountEntity discount = discountRepository.findById(productDTO.getDiscount());
        if(discount != null){
            productsEntitys.setDiscountEntitys(discount);

        }
        //category
        categoryEntity  category = categoryService.findOnecategory(productDTO.getCategory());
        productsEntitys.setCategoryEntity(category);
        productsEntitys.setImageEntities(imageEntityList);

        List<productdetailEntity> productdetail = productDTO.getListsize().stream().map(
                productdetalDTO -> {
                    productdetailEntity productdetailEntity = modelMapper.map(productdetalDTO, api.entity.productdetailEntity.class);
                    productdetailEntity.setProductsEntity(finalproduct);
                    return productdetailEntity;
                }
        ).collect(Collectors.toList());

        productsEntitys.setProductdetailEntities(productdetail);
        //create product
        productsRepository.save(productsEntitys);

        productsDTO pro = parseProductDTO(productsEntitys);
        return  pro;
    }



    public List getListProductsActive(){
        List<productsDTO> list = productsRepository.getlistproductactive().stream().map(
                productsEntity -> {
                    productsDTO productDTO = modelMapper.map(productsEntity,productsDTO.class);
                    return productDTO;
                }
        ).collect(Collectors.toList());

        return  list;
    }


    public productsDTO getproduct(long id){
        productsEntity productsEntity = productsRepository.findById(id);
        productsDTO productDTO = parseProductDTO(productsEntity);
        return productDTO;
    }


    public List<productsDTO> getlistproductpagination(Pageable pageable){
        List<productsDTO> list =  parseList(productsRepository.findAll(pageable).getContent());
        return  list;
    }

    public int totalpage(){
        return (int) productsRepository.count();
    }


    public List<productsDTO> parseList(List<productsEntity> list){
        List<productsDTO> listDTO = list.stream().map(
                productsEntity -> {
                    productsDTO productDTO = parseProductDTO(productsEntity);
                    return productDTO;
                }

        ).collect(Collectors.toList());
        return listDTO;
    }


    public productsDTO parseProductDTO(productsEntity productsEntity){
        productsDTO productDTO = modelMapper.map(productsEntity,productsDTO.class);
        List<productdetailDTO> listproductdetail = productdetailRepository.findByProductid(productDTO.getId()).stream().map(
                productdetailEntity -> {
                    productdetailDTO productdetail = modelMapper.map(productdetailEntity,productdetailDTO.class);
                    productdetail.setProductid(productdetailEntity.getProductsEntity().getId());
                    return  productdetail;
                }
        ).collect(Collectors.toList());
        List<imageDTO> listimage = imageRepository.findByProductid(productDTO.getId()).stream().map(
                imageEntity -> {
                    imageDTO imageDTO = modelMapper.map(imageEntity, api.DTO.imageDTO.class);
                    imageDTO.setProductid(imageEntity.getProductsEntity().getId());
                    return  imageDTO;
                }
        ).collect(Collectors.toList());;
        productDTO.setCategory(productsEntity.getCategoryEntity().getId());
        productDTO.setListimage(listimage);
        productDTO.setListsize(listproductdetail);
        return productDTO;
    }

}
