package jm.component;

import jm.UserService;
import jm.config.FileStorageProperties;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    private UserService userService;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try { Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) { throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex); }
    }

    public ResponseEntity<?> saveFile(MultipartFile file, long userId){
        String newAvatarName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try{
            if(newAvatarName.contains("..")){ return new ResponseEntity<>("Invalid path sequence: " + newAvatarName, HttpStatus.BAD_REQUEST); }
            User user = userService.getUserById(userId);
            if(user == null){ return new ResponseEntity<>("User not found with id: " + userId, HttpStatus.NOT_FOUND); }
            newAvatarName = userId + newAvatarName;
            Path newAvatarLocation = this.fileStorageLocation.resolve(newAvatarName);
            Files.copy(file.getInputStream(), newAvatarLocation, StandardCopyOption.REPLACE_EXISTING);
            String currentAvatarName = user.getAvatarURL();

            if(currentAvatarName != null){
                Path currentAvatarLocation = this.fileStorageLocation.resolve(currentAvatarName);
                Files.delete(currentAvatarLocation);
            }
            user.setAvatarURL(newAvatarName);
            userService.updateUser(user);
            System.out.println(user.getAvatarURL());

            return new ResponseEntity<>("Avatar changed for user with id: " + userId, HttpStatus.CREATED);
        } catch (IOException ex){
            return new ResponseEntity<>("Avatar not changed for user with id: " + userId, HttpStatus.NOT_MODIFIED);
        }
    }

    public ResponseEntity<?> deleteFile(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null){ return new ResponseEntity<>("User not found with id: " + userId, HttpStatus.NOT_FOUND); }
        String currentAvatarName = user.getAvatarURL();
        if (currentAvatarName == null){return new ResponseEntity<>("No avatar present for user with id: " + userId, HttpStatus.OK); }

        try {
            user.setAvatarURL(null);
            userService.updateUser(user);
            Path currentAvatarLocation = this.fileStorageLocation.resolve(currentAvatarName);
            Files.delete(currentAvatarLocation);
            return new ResponseEntity<>("Avatar deleted for user with id: " + userId, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Could not delete avatar for user with id: " + userId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}