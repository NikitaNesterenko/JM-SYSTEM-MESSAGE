package jm;

import java.util.UUID;

public class HashService {

    public HashService() {
    }

    public String generateHash(int length) {
        StringBuilder randomStr = new StringBuilder(UUID.randomUUID().toString());
        while(randomStr.length() < length) {
            randomStr.append(UUID.randomUUID().toString());
        }
        return randomStr.toString().replace("-", "").substring(0, length);
    }

}
