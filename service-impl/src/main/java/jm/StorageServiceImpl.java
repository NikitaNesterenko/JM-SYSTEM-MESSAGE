package jm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class StorageServiceImpl  implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
    private long count;

    @Value("${upload.directory}")
    String uploadPath;

    @Value("${avatar.upload.directory}")
    String avatarPath;

    @Override
    public String store(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get(uploadPath);

        /**TODO решить, как и где хранить файлы */

        /*//Костыльное удаление папки при первом сохранении с рестарта
        if (count++ < 1) {
            File dir = new File(uploadPath);
            String[] entries = dir.list();
            assert entries != null;
            for(String s: entries){
                File currentFile = new File(dir.getPath(),s);
                currentFile.delete();
            }
            Files.deleteIfExists(uploadDir);
        }*/

        if (!Files.exists(uploadDir)) {
            Files.createDirectory(uploadDir);
        }
        String fileName = file.getOriginalFilename();

        file.transferTo(Paths.get(uploadPath + "/" + fileName));
        logger.info("File upload was successful");
        return fileName;
    }

    public Path load(String filename) {
        return Paths.get(uploadPath).resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Resource loadAvatarAsResource(String userId) throws IOException {
        try {
            Path file = Files.list(Paths.get(avatarPath))
                    .filter(data -> data.getFileName().toString().startsWith(userId + ".")).findFirst()
                    //если аватара в системе нет, то вернуть дефолтный аватар
                    .orElse(Paths.get(avatarPath).resolve("blank_user.png"));
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String storeAvatars(MultipartFile file, Long userId) throws IOException {
        /*TODO решить, где хранить аватарки*/

        Path uploadDir = Paths.get(avatarPath);

        if (!Files.exists(uploadDir)) {
            Files.createDirectory(uploadDir);
        }

        Files.list(uploadDir).filter(avatar -> avatar.getFileName().toString().startsWith(userId + ".")).forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        String fileName = userId + Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf("."));

        file.transferTo(Paths.get(uploadDir + File.separator + fileName));

        logger.info("Avatar upload was successful");
        return fileName;
    }
}
