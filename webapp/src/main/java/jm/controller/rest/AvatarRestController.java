package jm.controller.rest;

import jm.component.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rest/api/avatar")
public class AvatarRestController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/{userId}")
    public ResponseEntity saveAvatar(@RequestParam("file") MultipartFile file, @PathVariable long userId){
        return fileStorageService.saveFile(file, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteFile(@PathVariable long id){
        return fileStorageService.deleteFile(id);
    }
}