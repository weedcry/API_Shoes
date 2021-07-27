package api.controller;

import api.DTO.FileResponse;
import api.config.AmazonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/image")
public class imageController {
    private AmazonClient amazonClient;


    @PostMapping("/uploadFile")
    public ResponseEntity<FileResponse> uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return new ResponseEntity<FileResponse>(amazonClient.uploadFile(file), HttpStatus.OK);
    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }

}