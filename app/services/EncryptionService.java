package services;

import com.typesafe.config.Config;
import org.jasypt.util.text.AES256TextEncryptor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EncryptionService {
    public static String secretKey;

    @Inject
    public EncryptionService(Config config) {
        secretKey = config.getString("encryption_key");
    }

    public static String encrypt(String plain) {
        AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
        aesEncryptor.setPassword(secretKey);
        return aesEncryptor.encrypt(plain);
    }

    public static String decrypt(String encrypted) {
        try {
            AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
            aesEncryptor.setPassword(secretKey);
            return aesEncryptor.decrypt(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
