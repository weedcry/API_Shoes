package api.controller;

import api.DTO.usersDTO;
import api.service.sendMailService;
import api.service.usersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/users")
public class usersController {
    @Autowired
    usersService usersService;

    @Autowired
    sendMailService sendMailService;

    @GetMapping("")
       public ResponseEntity<Object> getusers(){
//        String username = "";
        String username = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        }

            usersDTO usersDTO = usersService.getusers(username);
            if(usersDTO == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            return ResponseEntity.status(HttpStatus.OK).body(usersDTO);
        }

    @PostMapping("/mail/{mailto}")
    public ResponseEntity<Object> getusers(@PathVariable String mailto) throws MessagingException {
        sendMailService.sendHtmlWelcomeEmail(mailto+"@gmail.com","welcome");

        return ResponseEntity.status(HttpStatus.OK).body("success");
    }


}
