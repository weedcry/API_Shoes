package api.controller;

import api.DTO.ResultPage;
import api.DTO.productsDTO;
import api.service.productsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class productsController {
    @Autowired
    productsService productsService;

    @GetMapping("/id/{productsid}")
    public ResponseEntity getproduct(@PathVariable long productsid){
        productsDTO productDTO = productsService.getproduct(productsid);
        if(productDTO == null){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found") ;
        }
        return ResponseEntity.status(HttpStatus.OK).body(productDTO);
    }


    @GetMapping("/user")
    public ResponseEntity getlistproductactive(){
        List<productsDTO> list = productsService.getListProductsActive();
        if(list.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("list products active not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/page")
    public ResponseEntity getlistproductpagination(
         @RequestParam(name = "page", defaultValue = "0") int page,
         @RequestParam(name = "size", defaultValue = "5") int size){
        ResultPage resultPage = new ResultPage();
        Pageable pageable = new PageRequest(page,size);
        resultPage.setPage(page);
        resultPage.setListResult(productsService.getlistproductpagination(pageable));
        resultPage.setTotalpage((int) Math.ceil((double) (productsService.totalpage()) / size));
        return ResponseEntity.ok().body(resultPage);
    }

    @PostMapping("")
    public ResponseEntity createproduct(@Valid @RequestBody productsDTO product){
        productsDTO productDTO = productsService.saveproducts(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    @PutMapping("")
    public ResponseEntity updateproduct(@Valid @RequestBody productsDTO product){

        productsDTO productDTO = productsService.saveproducts(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }


}
