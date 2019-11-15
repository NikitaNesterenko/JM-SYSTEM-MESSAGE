package jm.controller;

import jm.StorageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class StorageController {

    private StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        storageService.store(file);
        return "redirect:/";
    }

}
