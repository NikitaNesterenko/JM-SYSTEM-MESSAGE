package jm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageServiceImpl  implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    @Value("${upload.directory}")
    String uploadPath;

    @Override
    public String store(MultipartFile file) throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectory(uploadDir);
        }
        String fileName = file.getOriginalFilename();
        file.transferTo(Paths.get(uploadPath + "/" + fileName));
        logger.info("File upload was successful");
        return fileName;
    }
}
