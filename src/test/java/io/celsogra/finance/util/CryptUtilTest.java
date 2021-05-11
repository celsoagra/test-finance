package io.celsogra.finance.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
public class CryptUtilTest {
    
    private static final String SIGNATURE_ALGORITHM = "ECDSA";
    private static final String SIGNATURE_PROVIDER = "BC";
    private static final String STRING_TEST = "Test";
    private static final String STRING_TEST_RESULT = "532eaabd9574880dbf76b9b8cc00832c20a6ec113d682299550d7a6e0f345e25";
    private static final String PRIVATE_KEY = "MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBivWIhjbGFvI+S+uOddQvDzpDTENwJmk2OgCgYIKoZIzj0DAQGhNAMyAASIAMCXEyiBrKnJv/Xhm7gx2ZfkjaxWhsqFu5RTQ+Rd2tFZ7EMAEW6YJIWlYrs+hxs=";
    private static final String PUBLIC_KEY = "MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEiADAlxMogaypyb/14Zu4MdmX5I2sVobKhbuUU0PkXdrRWexDABFumCSFpWK7Pocb";
    private static final String APPLIED_SIGNATURE = "MDYCGQCXJP0cbU9ZPdax+9VSssnUwT4I1qUyPwwCGQDjbYGQXPvjrKAJh/XgZr2nV9g1wHrqxMA=";

    
    @BeforeAll
    static void beforeAll() {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @Test
    void testApplySha256MethodWithSuccess() {
        String sha256 = CryptUtil.applySha256(STRING_TEST);
        assertEquals(STRING_TEST_RESULT, sha256);
    }
    
    @Test
    void testApplyAndVeirySignarureWithSuccess() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        PrivateKey privateKey = generatePrivateKeyFromString(PRIVATE_KEY);
        PublicKey publiKey = CryptUtil.getKeyFromString(PUBLIC_KEY);
        byte[] signature = CryptUtil.applyECDSASig(privateKey, STRING_TEST);
        assertTrue(CryptUtil.verifyECDSASig(publiKey, STRING_TEST, signature));
    }
    
    public PrivateKey generatePrivateKeyFromString(String str) {
        PrivateKey key = null;
        try {
            byte[] encoded = Base64.getDecoder().decode(str);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            key = kf.generatePrivate(spec);
        } catch (Exception e) {
        }
        
        return key;
    }
    
}
