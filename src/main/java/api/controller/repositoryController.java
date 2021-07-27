package api.controller;

import api.DTO.repositoryDTO;
import api.entity.repositoryEntity;
import api.service.repositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/repository")
public class repositoryController {
    @Autowired
    repositoryService repositoryService;

    @GetMapping("/list")
    public ResponseEntity getListRepository(){
        List<ResponseEntity> list = repositoryService.getListRepository();
        if(list.size() == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


     @PostMapping("/create")
     public ResponseEntity createRepository(@Valid @RequestBody repositoryDTO repositoryDTO){
         repositoryEntity repository = repositoryService.createRepository(repositoryDTO);
        if(repository== null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository);
     }

}
