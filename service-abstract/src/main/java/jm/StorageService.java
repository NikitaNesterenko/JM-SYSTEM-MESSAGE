package jm;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile file) throws IOException;
}
