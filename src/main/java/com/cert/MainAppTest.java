package com.cert;

import org.junit.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * @author: tangJ
 * @Date: 2018/9/25 17:21
 * @Description: ֤��������
 */
public class MainAppTest {

    static String filePath = "./src/main/resources/keystore/test.jks";

    static String password = "123456";

    static String alias = "test";

    /**
     * ����֤��
     *
     * @throws Exception
     */
    @Test
    public void generateCert() throws Exception {
        CertBuilder genCert = CertBuilder.getInstance();
        CertAndKey certAndKey = genCert.generateCert();
        String base64 = Base64.getEncoder().encodeToString(certAndKey.getX509Certificate().getEncoded());
        System.out.println(base64);
        KeyStoreUtil.createKeyStore(filePath, "123456", "test", certAndKey.getPrivateKey(), certAndKey.getX509Certificate());
    }

    /**
     * ��keyStoreȡ��֤��
     *
     * @throws Exception
     */
    @Test
    public void getCertFromKeyStore() throws Exception {

        X509Certificate certificate = (X509Certificate) KeyStoreUtil.getCertFromJks(filePath, password, "");
        System.out.println(Base64.getEncoder().encodeToString(certificate.getEncoded()));

    }

    /**
     * ��KeyStoreȡ��˽Կ
     *
     * @throws Exception
     */
    @Test
    public void getPriKeyFromKeyStore() throws Exception {
        PrivateKey privateKey = KeyStoreUtil.getPriKeyFromJks(filePath, password, alias);
        System.out.println(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    }


}
