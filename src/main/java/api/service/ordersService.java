package api.service;

import api.DTO.*;
import api.entity.*;
import api.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


    public List<ordersDTO> getListOrderCustomer(String userid){
        customersEntity customers = customersRepository.findByUsers_id(userid);
        List<ordersDTO> list = ordersRepository.findListOrderCustomer(customers.getId()).stream().map(
                ordersEntity -> {
//                    orderdetailEntity  orderdetail  =  orderdetailRepository.findOrderdetail(ordersEntity.getId());
                    ordersDTO orders = modelMapper.map(ordersEntity,ordersDTO.class);
//                    orders.setPaymentEntity(ordersEntity.getPaymentEntity().getId());

                    // orderdetail dto
                    List<orderdetailDTO> listOrderdetailDTO = ordersEntity.getOrderdetailEntities().stream().map(
                            orderdetailEntity -> {
                                orderdetailDTO orderdetailDTO = modelMapper.map(orderdetailEntity, api.DTO.orderdetailDTO.class);
                                orderdetailDTO.setProductid(orderdetailEntity.getProductdetailEntity().
                                        getProductsEntity().getId());
                                orderdetailDTO.setSize(orderdetailEntity.getProductdetailEntity().getSize());
                                return  orderdetailDTO;
                            }
                    ).collect(Collectors.toList());
                    orders.setListOrderdetail(listOrderdetailDTO);
                    return orders;
                }
        ).collect(Collectors.toList());
        return list;
    }

    public List<ordersDTO> getListOrderAdmin(){
        List<ordersDTO> list = ordersRepository.findAll().stream().map(
                ordersEntity -> {
                    orderdetailEntity  orderdetail  =  orderdetailRepository.findOrderdetail(ordersEntity.getId());
                    ordersDTO orders = modelMapper.map(ordersEntity,ordersDTO.class);
//                    orders.setPaymentEntity(ordersEntity.getPaymentEntity().getId());

                    // orderdetail dto
                    List<orderdetailDTO> listOrderdetailDTO = ordersEntity.getOrderdetailEntities().stream().map(
                            orderdetailEntity -> {
                                orderdetailDTO orderdetailDTO = modelMapper.map(orderdetailEntity, api.DTO.orderdetailDTO.class);
                                orderdetailDTO.setProductid(orderdetailEntity.getProductdetailEntity().
                                        getProductsEntity().getId());
                                return  orderdetailDTO;
                            }
                    ).collect(Collectors.toList());
                    orders.setListOrderdetail(listOrderdetailDTO);
                    return orders;
                }
        ).collect(Collectors.toList());
        return list;
    }



    public boolean createOrders(List<shopcartDTO> listshopcartDTO,String username){
        customersEntity customersEntity = customersRepository.findByUsers_id(username);
        List<orderdetailEntity> listOrderDetail = new ArrayList<>();
        float total = 0;
        for (shopcartDTO shopcartDTO :listshopcartDTO) {
            shopcartEntity shopcart = shopcartRepository.findById(shopcartDTO.getId());
            if(shopcart == null || !shopcart.getCustomers().getUsersEntitys().getUsername().equals(username)){
                return false;
            }

            //price
            float price = (shopcart.getProductdetail().getProductsEntity().getPrice()*shopcart.getQuantity());
            if(shopcart.getProductdetail().getProductsEntity().getDiscountEntitys() != null){
                float discount = price * shopcart.getProductdetail().getProductsEntity().getDiscountEntitys().getPercent();
                price -= discount;
            }
            total += price;
            shopcartRepository.delete(shopcart);

            //create orderdetail
            orderdetailEntity  orderdetail = new orderdetailEntity();
            orderdetail.setProductdetailEntity(shopcart.getProductdetail());
            orderdetail.setQuantity(shopcart.getQuantity());
            listOrderDetail.add(orderdetail);
        }

        //create order
        // id
        Date date = new Date();
        String id = customersEntity.getUsersEntitys().getUsername().substring(0,2)+
                String.valueOf(customersEntity.getId())+date.getTime();

        ordersEntity ordersEntity = new ordersEntity();
        ordersEntity.setId(id);
        ordersEntity.setAddress(customersEntity.getAddress());
        ordersEntity.setCreatedDate(date);
        ordersEntity.setEmail(customersEntity.getUsersEntitys().getEmail());
        ordersEntity.setFullname(customersEntity.getLastname() +" "+customersEntity.getFirstname());
        ordersEntity.setPhone(customersEntity.getPhone());
        ordersEntity.setStatus(api.entity.ordersEntity.Status.UNCONFIRM);
        ordersEntity.setCustomersEntity(customersEntity);
//        ordersEntity.setPaymentEntity();
        ordersEntity.setTotal(total);
//        ordersEntity.setOrderdetailEntities(listOrderDetail);
        final ordersEntity finalOrder =  ordersRepository.save(ordersEntity);

        // cascade type ALl not working , i will fix
        for (orderdetailEntity order : listOrderDetail) {
            order.setOrders(finalOrder);
            orderdetailRepository.save(order);
        }
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
        productDTO.setCategoryid(productsEntity.getCategory().getId());
        productDTO.setCategoryname(productsEntity.getCategory().getName());
        productDTO.setListimage(listimage);
        productDTO.setListsize(listproductdetail);
        return productDTO;
    }




}
