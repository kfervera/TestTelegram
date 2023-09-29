package com.bit2bitamericas.jira.telegramIntegration.utils;

import com.atlassian.jira.workflow.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Crypt {
    private static final Logger log = LoggerFactory.getLogger(Crypt.class);
    public static String SECRET_KEY = "7pOMECidjONMja9S";
    private static Cipher ecipher;
    private static Cipher dcipher;
    private static SecretKey key;

    private static void Initialise() {
        try {
            key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public static String encrypt(String str) {
        try {
            Initialise();
            log.debug("Encriptando string...");
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            byte[] enc = ecipher.doFinal(bytes);
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public static String decrypt(String str) {
        try {
            Initialise();
            log.debug("Desencriptando string...");
            byte[] bytes = Base64.getDecoder().decode(str);
            byte[] dec = dcipher.doFinal(bytes);
            return new String(dec, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }
}
