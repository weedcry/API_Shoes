package api.controller;

import api.DTO.ordersDTO;
import api.DTO.shopcartDTO;
import api.service.ordersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/orders")
public class ordersController {
    @Autowired
    ordersService ordersService;


    @GetMapping("/user")
    public ResponseEntity getListOrderCustomer(){
        String username = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        }

        List<ordersDTO> list = ordersService.getListOrderCustomer(username);
        if(list.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
            return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/admin")
    public ResponseEntity getListOrderAdmin(){
        List<ordersDTO> list = ordersService.getListOrderAdmin();
        if(list.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


    @PostMapping("/create")
    public ResponseEntity createOrder(@Valid @RequestBody List<shopcartDTO> shopcart){
        String username = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        }

        if(ordersService.createOrders(shopcart,username)){
           return ResponseEntity.status(HttpStatus.CREATED).body("success");
       }
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
    }



    @PostMapping("/cancel")
    public ResponseEntity cancelOrder(@Valid @RequestBody ordersDTO order){
        Boolean check = ordersService.cancelOrder(order);
        if(check){
            return ResponseEntity.status(HttpStatus.OK).body("success");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
    }


    @PostMapping("/accept")
    public ResponseEntity acceptOrder(@Valid @RequestBody ordersDTO order){
        Boolean check = ordersService.acceptOrder(order);
        if(check){
            return ResponseEntity.status(HttpStatus.OK).body("success");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
    }

    @PostMapping("/confirm")
    public ResponseEntity confirmOrder(@Valid @RequestBody ordersDTO order){
        Boolean check = ordersService.confirmOrder(order);
        if(check){
            return ResponseEntity.status(HttpStatus.OK).body("success");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
    }

}
