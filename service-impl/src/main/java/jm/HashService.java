package jm;

public class HashService {

    public HashService() {
    }

    public int generateHash(int hashLength) {
        int hash = 0;
        for (int i = 0; i < hashLength; i++) {
            hash = hash * 31 + i;
        }
        return hash;
    }

}
