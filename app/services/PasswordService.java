package services;

import com.typesafe.config.Config;
import org.apache.commons.codec.digest.DigestUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PasswordService {

    @Inject
    public PasswordService(Config config) {

    }

    public static String hashPassword(String original) {
        return DigestUtils.sha256Hex(original);
    }

    public static boolean checkPassword(String original, String hashed) {
        String newHashed = hashPassword(original);
        return newHashed.equals(hashed);
    }
}
