package com.cert;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 * @author: tangJ
 * @Date: 2018/9/25 17:20
 * @Description:
 */
public class KeyStoreUtil {

    /**
     *
     * @param filename
     * @param password
     * @param alias
     * @param privateKey
     * @param cert
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static void createKeyStore(String filename,
                                      String password, String alias,
                                      Key privateKey, Certificate cert) throws GeneralSecurityException, IOException {
        KeyStore ks = createEmptyKeyStore();
        ks.setKeyEntry(alias, privateKey, password.toCharArray(), new Certificate[]{cert}
        );
        saveKeyStore(ks, filename, password);
    }

    /**
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static KeyStore createEmptyKeyStore() throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        return ks;
    }

    /**
     *
     * @param ks
     * @param filename
     * @param password
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static void saveKeyStore(KeyStore ks, String filename,
                                     String password) throws GeneralSecurityException, IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream out = new FileOutputStream(filename)) {
            ks.store(out, password.toCharArray());
        }
    }

    /**
     * @param filePath
     * @param password
     * @return
     */
    public static KeyStore getKeyStore(String filePath, String password) {
        FileInputStream fis = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
            File file = new File(filePath);
            fis = new FileInputStream(file);
            keyStore.load(fis, password.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }

            }
        }
        return keyStore;

    }

    /**
     * @param filePath
     * @param passWord
     * @param alias
     * @return
     * @throws Exception
     */
    public static Certificate getCertFromJks(String filePath, String passWord, String alias) throws Exception {
        KeyStore keyStore = getKeyStore(filePath, passWord);
        if (StringUtils.isBlank(alias)) {
            Enumeration<String> enumeration = keyStore.aliases();
            alias = enumeration.nextElement();
        }
        Certificate certificate = keyStore.getCertificate(alias);
        return certificate;
    }

    /**
     * @param filePath
     * @param passWord
     * @param alias
     * @return
     * @throws Exception
     */
    public static PrivateKey getPriKeyFromJks(String filePath, String passWord, String alias) throws Exception {
        KeyStore keyStore = getKeyStore(filePath, passWord);
        PrivateKey key = (PrivateKey) keyStore.getKey(alias, passWord.toCharArray());
        return key;
    }

}
