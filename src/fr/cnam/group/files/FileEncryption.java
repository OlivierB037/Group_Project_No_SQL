package fr.cnam.group.files;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class FileEncryption {


    private SecretKeySpec keySpec;

    public FileEncryption(char[] _password, byte[] _salt, int _iterations, int _keyLength) throws Exception {

        keySpec = createSecretKey(_password,_salt,_iterations,_keyLength);


    }



    public SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        AlgorithmParameters parameters = cipher.getParameters();
        IvParameterSpec parameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] textBytes = cipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = parameterSpec.getIV();
        return EncodeBytes(iv) + ":" + EncodeBytes(textBytes);
    }



    public String decrypt(String string) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(DecodeBytes(iv)));
        return new String(cipher.doFinal(DecodeBytes(property)), "UTF-8");
    }
    private String EncodeBytes(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private byte[] DecodeBytes(String property) {
        return Base64.getDecoder().decode(property);
    }
}

