package jm.controller;

import jm.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@RestController
public class StorageController {

    private StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam MultipartFile file) throws IOException {
        System.out.println("UPLOAD CONTROLLER " + file);
        System.out.println("UPLOAD CONTROLLER " + file.getSize());
        System.out.println("UPLOAD CONTROLLER " + file.getResource());
        System.out.println("UPLOAD CONTROLLER " + file.getOriginalFilename());
        System.out.println("UPLOAD CONTROLLER " + file.getName());
        System.out.println("UPLOAD CONTROLLER " + file.getContentType());
        System.out.println("UPLOAD CONTROLLER " + Arrays.toString(file.getBytes()));
        String filename = storageService.store(file);
        return new ResponseEntity<>(filename, HttpStatus.OK);
    }
}
