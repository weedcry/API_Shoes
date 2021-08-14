package api.service;

import api.DTO.ResultPage;
import api.DTO.imageDTO;
import api.DTO.productdetailDTO;
import api.DTO.productsDTO;
import api.entity.*;
import api.exception.IdNotFoundException;
import api.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    categoryRepository categoryRepository;

    @Autowired
    discountRepository discountRepository;

    @Autowired
    productdetailRepository productdetailRepository;

    @Autowired
    imageRepository imageRepository;

    @Autowired
    discountService discountService;


    public productsDTO saveproducts(productsDTO productDTO,String username){
        if(!categoryService.checkid(productDTO.getCategoryid())){
            throw new IdNotFoundException("could not find category "+productDTO.getCategoryid());
        }

        productsEntity productsEntitys = productsRepository.findById(productDTO.getId());
        if(productsEntitys == null){
            productsEntitys = modelMapper.map(productDTO,productsEntity.class);
            productsEntitys.setCreateddate(new Date());
            productsEntitys.setCreatedby(username);
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
            productsEntitys.setModifieddate(new Date());
            productsEntitys.setModifiedby(username);
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
        categoryEntity  category = categoryService.findOnecategory(productDTO.getCategoryid());
        productsEntitys.setCategory(category);
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
        productDTO.setCategoryid(productsEntity.getCategory().getId());
        productDTO.setCategoryname(productsEntity.getCategory().getName());
        productDTO.setListimage(listimage);
        productDTO.setListsize(listproductdetail);
        return productDTO;
    }

    public ResultPage productPagination(int page, int size, String typeSort, Long categoryId ,  String orderBy ){
        ResultPage resultPage = new ResultPage();
        Pageable pageable  ;
        Page<productsEntity> Result ;
        if(categoryId != null){ // category product
            Sort sort;
            if(orderBy.toUpperCase().equals("DESC")){
                sort  = new Sort(Sort.Direction.DESC, typeSort);
            }else{
                sort = new Sort(Sort.Direction.ASC, typeSort);
            }

            pageable = new PageRequest(page - 1,size,sort);
            Result = productsRepository.findByCategory_id(categoryId,pageable);
        }else{ // all product
            Sort sort;
            if(orderBy.toUpperCase().equals("DESC")){
                sort  = new Sort(Sort.Direction.DESC, typeSort);
            }else{
                sort = new Sort(Sort.Direction.ASC, typeSort);
            }

            pageable = new PageRequest(page - 1,size,sort);
            Result = productsRepository.findAll(pageable);
        }

        resultPage.setPage(Result.getNumber()  + 1 );
        resultPage.setListResult(parseList(Result.getContent()));
        resultPage.setTotalpage(Result.getTotalPages());
        return resultPage;
    }


    public ResultPage productPaginationName(int page, int size, Optional<String> name,String typeSort, String orderBy ){
        ResultPage resultPage =  new ResultPage();
        Page<productsEntity> Result ;
        Pageable pageable ;
        Sort sort;
        if(orderBy.toUpperCase().equals("DESC")){
            sort  = new Sort(Sort.Direction.DESC, typeSort);
        }else{
            sort = new Sort(Sort.Direction.ASC, typeSort);
        }
        pageable = new PageRequest(page - 1,size,sort);
        Result = productsRepository.findByNameContaining(name.get(),pageable);

        if(Result.getSize() == 0){
            return resultPage;
        }

        resultPage.setPage(Result.getNumber()  + 1 );
        resultPage.setListResult(parseList(Result.getContent()));
        resultPage.setTotalpage(Result.getTotalPages());
        return resultPage;
    }


}
