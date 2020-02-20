package jm;

import java.util.UUID;

public class UuidGenerator {
    public static String createStringUUID() {
        return UUID.randomUUID().toString();
    }
}
