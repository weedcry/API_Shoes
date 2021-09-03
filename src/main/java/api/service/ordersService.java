package api.service;

import api.DTO.*;
import api.entity.*;
import api.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sun.swing.BakedArrayList;

import javax.mail.MessagingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    paymentRepository paymentRepository;

    @Autowired
    api.service.sendMailService sendMailService;


    public List<ordersDTO> getListOrderCustomer(String userid){
        List<ordersDTO> list = new ArrayList<>();
        customersEntity customers = customersRepository.findByUsername(userid);
        if(customers == null){
            return list;
        }

        list = ordersRepository.findListOrderCustomer(customers.getId()).stream().map(
                ordersEntity -> {
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

    public ResultPageOrder getListOrderPageAdmin(int page, int size, Optional<String> title,
                                                 String typeSort, String orderBy, Optional<String> type ){
        ResultPageOrder resultPage = new ResultPageOrder();
        Pageable pageable  ;
        Page<ordersEntity> Result ;
        ordersEntity.Status  status ;
        if(ordersEntity.Status.UNCONFIRM.toString().equals(type.get())){
            status = ordersEntity.Status.UNCONFIRM;
        }else if(ordersEntity.Status.CANCEL.toString().equals(type.get())){
            status = ordersEntity.Status.CANCEL;
        }
        else if(ordersEntity.Status.DELIVERING.toString().equals(type.get())){
            status = ordersEntity.Status.DELIVERING;
        }else{
            status = ordersEntity.Status.DELIVERED;
        }

        if(title.isPresent()){ //
            Sort sort;
            if(orderBy.toUpperCase().equals("DESC")){
                sort  = new Sort(Sort.Direction.DESC, typeSort);
            }else{
                sort = new Sort(Sort.Direction.ASC, typeSort);
            }

            pageable = new PageRequest(page - 1,size,sort);
            Result = ordersRepository.findByIdAndStatus(title.get(),status,pageable);
        }else{ // all
            Sort sort;
            if(orderBy.toUpperCase().equals("DESC")){
                sort  = new Sort(Sort.Direction.DESC, typeSort);
            }else{
                sort = new Sort(Sort.Direction.ASC, typeSort);
            }
            pageable = new PageRequest(page - 1,size,sort);
            Result = ordersRepository.findByStatus(status,pageable);
        }

        resultPage.setPage(Result.getNumber()  + 1 );
        resultPage.setListResult( parseListOrderDTO(Result.getContent()));
        resultPage.setTotalpage(Result.getTotalPages());
        return resultPage;
    }



    public boolean createOrders(infoOrderDTO infoOrder,String username) throws MessagingException {
        customersEntity customersEntity = customersRepository.findByUsers_id(username);
        List<orderdetailEntity> listOrderDetail = new ArrayList<>();
        float total = 0;
        for (shopcartDTO shopcartDTO : infoOrder.getShopcart()) {
            shopcartEntity shopcart = shopcartRepository.findById(shopcartDTO.getId());
            if(shopcart == null || !shopcart.getCustomers().getUsersEntitys().getUsername().equals(username)){
                return false;
            }

            //price
            float price = (shopcart.getProductdetail().getProductsEntity().getPrice()*shopcart.getQuantity());
            if(shopcart.getProductdetail().getProductsEntity().getDiscountEntitys() != null){
                float discount = price * (shopcart.getProductdetail().getProductsEntity().getDiscountEntitys().getPercent()/100);
                price -= discount;
            }

            total += price;
            shopcartRepository.delete(shopcart);

            // inventory product
            productdetailEntity productdetail = productdetailRepository.findById(shopcartDTO.getProductdetail().getId());
            if(productdetail.getInventory() == 0){
                return false;
            }
            long valueNew = productdetail.getInventory() - shopcartDTO.getQuantity();
            if(valueNew < 0){
                return false;
            }
            productdetail.setInventory(valueNew);
            productdetailRepository.save(productdetail);

            //create orderdetail
            orderdetailEntity  orderdetail = new orderdetailEntity();
            orderdetail.setProductdetailEntity(shopcart.getProductdetail());
            orderdetail.setQuantity(shopcart.getQuantity());
            orderdetail.setPrice(price);
            listOrderDetail.add(orderdetail);
        }

        //create order
        // id
        Date date = new Date();
        String id = customersEntity.getUsersEntitys().getUsername().substring(0,2)+
                String.valueOf(customersEntity.getId())+date.getTime();

        ordersEntity ordersEntity = new ordersEntity();
        ordersEntity.setId(id);
        ordersEntity.setAddress(infoOrder.getAddress());
        ordersEntity.setCreatedDate(date);
        ordersEntity.setEmail(infoOrder.getEmail());
        ordersEntity.setFullname(infoOrder.getFullname());
        ordersEntity.setPhone(infoOrder.getPhone());
        ordersEntity.setStatus(api.entity.ordersEntity.Status.UNCONFIRM);
        ordersEntity.setCustomersEntity(customersEntity);
        ordersEntity.setPaymentEntity(paymentRepository.findById(infoOrder.getPaymentid()));
        ordersEntity.setTotal(total);
//        ordersEntity.setOrderdetailEntities(listOrderDetail);
        final ordersEntity finalOrder =  ordersRepository.save(ordersEntity);

        // cascade type ALl not working , i will fix
        for (orderdetailEntity order : listOrderDetail) {
            order.setOrders(finalOrder);
            orderdetailRepository.save(order);
        }

//        sendMailService.sendHtmlEmail(finalOrder.getEmail(),"Đơn Hàng "+finalOrder.getCreatedDate(),finalOrder);
        return true;
    }

    public Boolean cancelOrder(ordersDTO orderDTO,String username){
        ordersEntity order = ordersRepository.findById(orderDTO.getId());
        if(order == null){
            return false;
        }

        for (orderdetailEntity orderdetail : order.getOrderdetailEntities())
        {
            // inventory product
            productdetailEntity productdetail = orderdetail.getProductdetailEntity();
            long valueNew = productdetail.getInventory() + orderdetail.getQuantity();
            productdetail.setInventory(valueNew);
            productdetailRepository.save(productdetail);
        }

        // cancel order
        order.setStatus(ordersEntity.Status.CANCEL);
        order.setModifiedDate(new Date());
        order.setModifiedBy(username);
        ordersRepository.save(order);
        return true;
    }

    public Boolean acceptOrder(ordersDTO orderDTO,String username){
        ordersEntity order = ordersRepository.findById(orderDTO.getId());
        if(order == null){
            return false;
        }

        for (orderdetailEntity orderdetail : order.getOrderdetailEntities())
        {
            // inventory product
            productsEntity productsEntity = orderdetail.getProductdetailEntity().getProductsEntity();
            int valueNew = productsEntity.getRating() + orderdetail.getQuantity();
            productsEntity.setRating(valueNew);
            productsRepository.save(productsEntity);
        }
        // accept order
        order.setStatus(ordersEntity.Status.DELIVERING);
        order.setModifiedDate(new Date());
        order.setModifiedBy(username);
        ordersRepository.save(order);
        return true;
    }

    public Boolean confirmOrder(ordersDTO orderDTO,String username){
        ordersEntity order = ordersRepository.findById(orderDTO.getId());
        if(order == null){
            return false;
        }
        // confirm order
        order.setStatus(ordersEntity.Status.DELIVERED);
        order.setModifiedDate(new Date());
        order.setModifiedBy(username);
        ordersRepository.save(order);
        return true;
    }


    public  List<ordersDTO> parseListOrderDTO(List<ordersEntity> listOrder){
        List<ordersDTO> list = listOrder.stream().map(
                ordersEntity -> {
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


    public Object DoanhThu(String mode) throws ParseException {
        if(mode.equals("YEAR")){
            List<Float> listTotal = new ArrayList<>();
            Date date = new Date();
            ordersEntity.Status status = ordersEntity.Status.DELIVERED;
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year  = localDate.getYear();
            for(int i = 1;i<=12;i++){
                Date datefrom = new SimpleDateFormat("dd/MM/yyyy").parse("01/"+i+"/"+year);
                Date dateto = new SimpleDateFormat("dd/MM/yyyy").parse("31/"+i+"/"+year);
                List<ordersEntity> list = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status);
                float total = 0;
                for(ordersEntity order : list){
                    total+=order.getTotal();
                }
                listTotal.add(total);
            }
           return listTotal;
        }else{
            Date dateto = new Date();
            LocalDate localDate = dateto.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year  = localDate.getYear();
            int month = localDate.getMonthValue();
            Date datefrom = new SimpleDateFormat("dd/MM/yyyy").parse("01/"+month+"/"+year);
            ordersEntity.Status status = ordersEntity.Status.DELIVERED;
            List<ordersEntity> list = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status);
            float total = 0;
            for(ordersEntity order : list){
                total+=order.getTotal();
            }
            return total;
        }
    }

    public Object tinhTrangDonHang(String mode) throws ParseException {
        if(mode.equals("YEAR")){
            List<String> listTotal = new ArrayList<>();
            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year  = localDate.getYear();
            for(int i = 1;i<=12;i++){
                Date datefrom = new SimpleDateFormat("dd/MM/yyyy").parse("01/"+i+"/"+year);
                Date dateto = new SimpleDateFormat("dd/MM/yyyy").parse("31/"+i+"/"+year);
                ordersEntity.Status status = ordersEntity.Status.DELIVERED;
                List<ordersEntity> list = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status);
                ordersEntity.Status status1 = ordersEntity.Status.CANCEL;
                List<ordersEntity> list1 = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status1);
                listTotal.add( String.valueOf(list.size())+"-"+String.valueOf(list1.size()));
            }
            return listTotal;
        }else if(mode.equals("MONTH")){
            List<Integer> listTotal = new ArrayList<>();
            Date dateto = new Date();
            LocalDate localDate = dateto.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year  = localDate.getYear();
            int month = localDate.getMonthValue();
            Date datefrom = new SimpleDateFormat("dd/MM/yyyy").parse("01/"+month+"/"+year);
            ordersEntity.Status status = ordersEntity.Status.DELIVERED;
            List<ordersEntity> list = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status);
            listTotal.add(list.size());
            ordersEntity.Status status1 = ordersEntity.Status.CANCEL;
            List<ordersEntity> list1 = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status1);
            listTotal.add(list1.size());
            return  listTotal;
        }else {
            List<Integer> listTotal = new ArrayList<>();
            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int day = localDate.getDayOfMonth();
            int year  = localDate.getYear();
            int month = localDate.getMonthValue();
            System.out.println(day);
            Date datefrom = new SimpleDateFormat("dd/MM/yyyy").parse(day+"/"+month+"/"+year);
            Date dateto = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(day+"/"+month+"/"+year +" 23:59:59");

            ordersEntity.Status status = ordersEntity.Status.DELIVERED;
            List<ordersEntity> list = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status);
            listTotal.add(list.size());
            ordersEntity.Status status1 = ordersEntity.Status.CANCEL;
            List<ordersEntity> list1 = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status1);
            listTotal.add(list1.size());
            ordersEntity.Status status2 = ordersEntity.Status.DELIVERING;
            List<ordersEntity> list2 = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status2);
            listTotal.add(list2.size());
            ordersEntity.Status status3 = ordersEntity.Status.UNCONFIRM;
            List<ordersEntity> list3 = ordersRepository.findByModifiedDateBetweenAndStatus(datefrom,dateto,status3);
            listTotal.add(list3.size());
            return  listTotal;
        }
    }
}
