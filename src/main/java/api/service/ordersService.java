package api.service;

import api.DTO.*;
import api.entity.*;
import api.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Component
public class ordersService {
    @Autowired
    ordersRepository ordersRepository;

    @Autowired
    api.repository.productsRepository productsRepository;

    @Autowired
    api.repository.customersRepository customersRepository;

    @Autowired
    orderdetailRepository  orderdetailRepository;

    @Autowired
    shopcartRepository shopcartRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    imageRepository imageRepository;

    @Autowired
    productdetailRepository productdetailRepository;




    public List getListOrderCustomer(String userid){
        customersEntity customers = customersRepository.findByUsers_id(userid);
        List<ordersDTO> list = ordersRepository.findListOrderCustomer(customers.getId()).stream().map(
                ordersEntity -> {
                    orderdetailEntity  orderdetail  =  orderdetailRepository.findOrderdetail(ordersEntity.getId());
                    ordersDTO orders = modelMapper.map(ordersEntity,ordersDTO.class);
                    orders.setSize(orderdetail.getProductdetailEntity().getSize());
                    orders.setProduct(parseProductDTO(orderdetail.getProductdetailEntity().getProductsEntity()));
//                    orders.setPaymentEntity(ordersEntity.getPaymentEntity().getId());
                    return orders;
                }
        ).collect(Collectors.toList());
        return list;
    }

    public List getListOrderAdmin(){
        List<ordersDTO> list = ordersRepository.findAll().stream().map(
                ordersEntity -> {
                    orderdetailEntity  orderdetail  =  orderdetailRepository.findOrderdetail(ordersEntity.getId());
                    ordersDTO orders = modelMapper.map(ordersEntity,ordersDTO.class);
                    orders.setSize(orderdetail.getProductdetailEntity().getSize());
                    orders.setProduct(parseProductDTO(orderdetail.getProductdetailEntity().getProductsEntity()));
//                    orders.setPaymentEntity(ordersEntity.getPaymentEntity().getId());
                    return orders;
                }
        ).collect(Collectors.toList());
        return list;
    }



    public boolean createOrders(shopcartDTO shopcartDTO,String username){
        shopcartEntity shopcart = shopcartRepository.findById(shopcartDTO.getId());
        if(shopcart == null || !shopcart.getCustomers().getUsersEntitys().getUsername().equals(username)){
            return false;
        }

        //create order
        Date date = new Date();
        ordersEntity ordersEntity = new ordersEntity();
        String id =shopcart.getCustomers().getUsersEntitys().getUsername().substring(0,2)+
                String.valueOf(shopcart.getCustomers().getId())+date.getTime();
        ordersEntity.setId(id);
        ordersEntity.setAddress(shopcart.getCustomers().getAddress());
        ordersEntity.setCreatedDate(date);
        ordersEntity.setEmail(shopcart.getCustomers().getUsersEntitys().getEmail());
        ordersEntity.setFullname(shopcart.getCustomers().getLastname() +" "+shopcart.getCustomers().getFirstname());
        ordersEntity.setPhone(shopcart.getCustomers().getPhone());
        ordersEntity.setStatus(api.entity.ordersEntity.Status.UNCONFIRM);
        ordersEntity.setCustomersEntity(shopcart.getCustomers());
//        ordersEntity.setPaymentEntity();

        float total = (shopcart.getProductdetail().getProductsEntity().getPrice()*shopcart.getQuantity());
        if(shopcart.getProductdetail().getProductsEntity().getDiscountEntitys() != null){
            float discount = total * shopcart.getProductdetail().getProductsEntity().getDiscountEntitys().getPercent();
            total -= discount;
        }
        ordersEntity.setTotal(total);
        ordersEntity orders =  ordersRepository.save(ordersEntity);

        //create orderdetail
        orderdetailEntity  orderdetail = new orderdetailEntity();
        orderdetail.setProductdetailEntity(shopcart.getProductdetail());
        orderdetail.setOrdersEntity(orders);
        orderdetail.setQuantity(shopcartDTO.getQuantity());
        orderdetailRepository.save(orderdetail);
        shopcartRepository.delete(shopcart);
        return true;
    }

    public Boolean cancelOrder(ordersDTO orderDTO){
        ordersEntity order = ordersRepository.findById(orderDTO.getId());
        if(order == null){
            return false;
        }

        // cancel order
        order.setStatus(ordersEntity.Status.CANCEL);
        ordersRepository.save(order);
        return true;
    }

    public Boolean acceptOrder(ordersDTO orderDTO){
        ordersEntity order = ordersRepository.findById(orderDTO.getId());
        if(order == null){
            return false;
        }

        // cancel order
        order.setStatus(ordersEntity.Status.DELIVERING);
        ordersRepository.save(order);
        return true;
    }

    public Boolean confirmOrder(ordersDTO orderDTO){
        ordersEntity order = ordersRepository.findById(orderDTO.getId());
        if(order == null){
            return false;
        }

        // cancel order
        order.setStatus(ordersEntity.Status.DELIVERED);
        ordersRepository.save(order);
        return true;
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
