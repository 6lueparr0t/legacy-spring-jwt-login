package com.spring_jwt_server.common.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.Test;

public class JasyptConfigTest {
    @Test
    public void encryptTest() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setPoolSize(2);
        encryptor.setPassword("6lueparr0t"); //패스워드 암호화 키
        encryptor.setAlgorithm("PBEWithSHA256And128BitAES-CBC-BC");

        String plainText = "secret_key";
        String encryptedText = encryptor.encrypt(plainText);
        System.out.println("Enc:"+encryptedText);
    }

    @Test
    public void decryptTest() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setPoolSize(2);
        encryptor.setPassword("6lueparr0t"); //패스워드 암호화 키
        encryptor.setAlgorithm("PBEWithSHA256And128BitAES-CBC-BC");

        String encryptedText = "EEGopae3rh1QA/6zpkshRGPIhdyHR2cYkbEqsPFqqyw=";
        String decryptedText = encryptor.decrypt(encryptedText);
        System.out.println("Dec:"+decryptedText);
    }
}
