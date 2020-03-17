package jm.controller;

import jm.StorageService;
import jm.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.util.Objects;

@RestController
public class StorageController {

    private StorageService storageService;
    private final UserService userService;

    public StorageController(StorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<String> handleFileUpload(@RequestParam MultipartFile file) throws IOException {

        String filename = storageService.store(file);
        return new ResponseEntity<>(filename, HttpStatus.OK);
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> saveAvatar(@RequestParam MultipartFile file, Principal principal) throws IOException {
        Long id = userService.getUserByLogin(principal.getName()).getId();
        String filename = storageService.storeAvatars(file, id);
        return new ResponseEntity<>(filename, HttpStatus.OK);
    }

    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String fileName) throws IOException {
        Resource avatar = storageService.loadAvatarAsResource(fileName);
        return Objects.isNull(avatar) ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : ResponseEntity.ok(avatar);
    }
}
