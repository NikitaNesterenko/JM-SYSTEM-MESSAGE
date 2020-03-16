package jm;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile file) throws IOException;

    String store(MultipartFile file, String storage) throws IOException;

    Resource loadAsResource(String filename);
}
