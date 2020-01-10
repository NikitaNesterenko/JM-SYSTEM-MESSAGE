package jm.controller.rest;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/images")
public class imageRestController {

    @GetMapping(value = "/{userId}/{imageName}")
    public ResponseEntity<?> getUserImage(@PathVariable String userId, @PathVariable String imageName) throws IOException {
        try {
            InputStream in = getClass().getResourceAsStream("/static/image/" + userId + "/" + imageName);
            String[] nameAsArray = imageName.split("\\.");
            MediaType mediaType = MediaType.valueOf("image/" + nameAsArray[nameAsArray.length - 1]);
            return ResponseEntity.ok().contentType(mediaType).body(IOUtils.toByteArray(in));
        } catch (NullPointerException e) { throw new ResponseStatusException(HttpStatus.NOT_FOUND); }
    }
}
