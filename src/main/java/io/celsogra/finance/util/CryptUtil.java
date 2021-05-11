package io.celsogra.finance.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import io.celsogra.finance.exception.HashGenerationException;
import io.celsogra.finance.exception.InvalidPrivateOrPublicKeyException;
import io.celsogra.finance.exception.SignatureGenerationException;
import io.celsogra.finance.exception.SignatureNotInitializedProperlyException;

public class CryptUtil {

    private static final String ALG_SHA256 = "SHA-256";
    private static final String CHARSET_ENCODING_UTF8 = "UTF-8";
    private static final String SIGNATURE_ALGORITHM = "ECDSA";
    private static final String SIGNATURE_PROVIDER = "BC";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String EC_PARAMETER_NAME = "prime192v1";

    public static String applySha256(String input) {
        StringBuffer hexString = new StringBuffer();
        byte[] hash;
        
        try {
            hash = MessageDigest.getInstance(ALG_SHA256).digest(input.getBytes(CHARSET_ENCODING_UTF8));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new HashGenerationException();
        }
        return hexString.toString();
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {

        byte[] output = new byte[0];
        byte[] strByte = input.getBytes();

        try {
            Signature dsa = Signature.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            dsa.initSign(privateKey);
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (InvalidKeyException e) {
            throw new InvalidPrivateOrPublicKeyException();
        } catch (SignatureException e) {
            throw new SignatureNotInitializedProperlyException();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new SignatureGenerationException(
                    String.format("some problems has occured with signature generation algorithm: %s and provider: %s",
                            SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER));
        }

        return output;
    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        Signature ecdsaVerify = null;
        boolean verified = false;
        
        try {
            ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            verified = ecdsaVerify.verify(signature);
        } catch (InvalidKeyException e) {
            throw new InvalidPrivateOrPublicKeyException();
        } catch (SignatureException e) {
            throw new SignatureNotInitializedProperlyException();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new SignatureGenerationException(
                    String.format("some problems has occured with signature generation algorithm: %s and provider: %s",
                            SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER));
        }
        return verified;
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    public static PublicKey getKeyFromString(String str) {
        PublicKey key = null;
        try {
            byte[] encodedPublicKey = Base64.getDecoder().decode(str);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
            KeyFactory kf = KeyFactory.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            key = kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new SignatureGenerationException(
                    String.format("some problems has occured with signature generation algorithm: %s and provider: %s",
                            SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER));
        } catch (InvalidKeySpecException e) {
            throw new InvalidPrivateOrPublicKeyException();
        }
        
        return key;
    }
    
    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(EC_PARAMETER_NAME);
            keyGen.initialize(ecSpec, random);
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new SignatureGenerationException(
                    String.format("some problems has occured with signature generation algorithm: %s and provider: %s",
                            SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER));
        }
        
        return keyPair;
    }

}
