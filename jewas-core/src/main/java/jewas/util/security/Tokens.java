package jewas.util.security;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

/**
 * @author fcamblor
 */
public class Tokens {
    public static String generateUniqueToken(List<String> existingTokens){
        String generatedToken = null;

        do {
            try {
                String seedStr = new String(SecureRandom.getInstance("SHA1PRNG").generateSeed(20), Charset.forName("UTF-8"));
                generatedToken = Encryptions.encryptString(seedStr);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }while(existingTokens.contains(generatedToken));

        return generatedToken;
    }
}
