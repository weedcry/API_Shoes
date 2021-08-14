package api.controller;

import api.DTO.ResultPage;
import api.DTO.productsDTO;
import api.service.productsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

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
         @RequestParam(name = "page", defaultValue = "1") int page,
         @RequestParam(name = "size", defaultValue = "8") int size,
         @RequestParam(name = "sort", defaultValue = "createddate") String sortType,
         @RequestParam(name = "order", defaultValue = "ASC") String orderby,
         @RequestParam(name = "category", required = false) Long categoryid){
        return ResponseEntity.ok().body(productsService.productPagination(page,size,sortType,categoryid,orderby));
    }


    @GetMapping("/page/search")
    public ResponseEntity getlistproductpaginationSearch(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "8") int size,
            @RequestParam(name = "sort", defaultValue = "createddate") String sortType,
            @RequestParam(name = "order", defaultValue = "ASC") String orderBy,
            @RequestParam(name = "title") Optional<String> title ){
        ResultPage resultPage = productsService.productPaginationName(page,size,title,sortType,orderBy);
        if(resultPage == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" not found");
        }
        return ResponseEntity.ok().body(resultPage);
    }



    @PostMapping("")
    public ResponseEntity createproduct(@Valid @RequestBody productsDTO product){
        String username = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        }
        productsDTO productDTO = productsService.saveproducts(product,username);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    @PutMapping("")
    public ResponseEntity updateproduct(@Valid @RequestBody productsDTO product){
        String username = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        }
        productsDTO productDTO = productsService.saveproducts(product,username);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }


}
