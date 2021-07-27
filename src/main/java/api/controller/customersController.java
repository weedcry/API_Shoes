package api.controller;

import api.DTO.customersDTO;
import api.entity.roleEntity;
import api.entity.role_name;
import api.repository.roleRepository;
import api.service.customersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class customersController {
    @Autowired
    customersService customersService;

    @Autowired
    roleRepository roleRepository;

    @GetMapping("")
    public ResponseEntity<Object> getcustomers(){
        String username = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        }
        customersDTO customersDTO =  customersService.getcustomers(username);
        if(customersDTO == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customersDTO);
    }


    @PostMapping("")
    public ResponseEntity<Object> updatecustomers(@Valid customersDTO customerDTO){
        String username = "";
        customersDTO customersDTO = customersService.updatecustomer(customerDTO,username);
        return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
    }

}
