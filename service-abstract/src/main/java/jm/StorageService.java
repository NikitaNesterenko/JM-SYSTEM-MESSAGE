package jm;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile file) throws IOException;

    String storeAvatars(MultipartFile file, Long userId) throws IOException;

    Resource loadAsResource(String filename);

    Resource loadAvatarAsResource(String filename) throws IOException;
}
