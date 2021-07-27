package api.controller;

import api.entity.discountEntity;
import api.service.discountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/discount")
public class discountController {
    @Autowired
    discountService discountService;


    @GetMapping("")
    public ResponseEntity getListDiscount(){
        List<discountEntity> list = discountService.getListDiscount();
        if(list.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found discount");
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


    @PostMapping("")
    public ResponseEntity createDiscount(@Valid @RequestBody discountEntity discountEntitys){
       discountEntity discount =  discountService.createDiscount(discountEntitys);
        if(discount == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id is already taken");
        }
        return ResponseEntity.status(HttpStatus.OK).body(discount);
    }


}
