package api.service;

import api.payload.request.SignupRequest;
import api.DTO.customersDTO;
import api.DTO.usersDTO;
import api.entity.customersEntity;
import api.entity.usersEntity;
import api.repository.customersRepository;
import api.repository.usersRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class customersService {
    @Autowired
    customersRepository customersRepository;
    @Autowired
    usersService usersService;
    PasswordEncoder encoder;

    @Autowired
    usersRepository usersRepository;

    @Autowired
    ModelMapper mapper;


    public int createcustomer(SignupRequest signRes,usersEntity user){
        // tạo customers
        String linkphotodefault = "https://s3.us-east-2.amazonaws.com/myawsbucketappfile/1622470096048-avatar.png";
        customersEntity customersEntity = new customersEntity(signRes.getFirstname(),signRes.getLastname(),signRes.getAddress(),
                signRes.getPhone(),linkphotodefault,user);
        customersRepository.save(customersEntity);
        return  1;
    }




    public customersDTO getcustomers(String username){
        customersEntity customersEntity = customersRepository.findByUsers_id(username);
        customersDTO customersDTO = null;
        if(customersEntity == null){
            return customersDTO;
        }
         customersDTO = mapper.map(customersEntity, api.DTO.customersDTO.class);
        return customersDTO;
    }


    public customersDTO updatecustomer(customersDTO customerDTO,String username){
//        usersDTO usersDTO = usersService.getusers(username);
        usersEntity usersEntity = usersRepository.finduser(username);
        customersEntity customersEntity = mapper.map(customerDTO, api.entity.customersEntity.class);
        customersEntity.setUsersEntitys(usersEntity);
        customersRepository.save(customersEntity);
        return customerDTO;
    }



}
