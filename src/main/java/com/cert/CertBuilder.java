package com.cert;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author: tangJ
 * @Date: 2018/9/25 17:17
 * @Description:
 */
public class CertBuilder {


    private static String alg = "SHA256WITHRSA";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static KeyPairGenerator kpg = null;

    private CertBuilder() {
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
        }
    }

    private static class Inner {
        static CertBuilder instance = new CertBuilder();
    }

    public static CertBuilder getInstance() {
        return Inner.instance;
    }



    public CertAndKey generateCert() {
        KeyPair keyPair = kpg.generateKeyPair();
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey priKey = keyPair.getPrivate();


        Date beginDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 3);

        Date expireDate = calendar.getTime();


        BigInteger serialNumber = BigInteger.probablePrime(32, new Random());



        String isuuerString = "CN=ROOTCA,L=shanghai,ST=shanghai";
        X500Name issuerDN = new X500Name(isuuerString);
        X500Name subjectDN = new X500Name(isuuerString);

        final byte[] signData;
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(priKey);
            signature.update(pubKey.getEncoded());
            signData = signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("֤��ǩ��ʧ��");
        }

        SubjectPublicKeyInfo subjectPublicKeyInfo = null;
        try {
            subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(new ASN1InputStream(pubKey.getEncoded()).readObject());
        } catch (IOException e) {
            System.out.println("֤�����⹹��ʧ��");
        }

        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuerDN, serialNumber, beginDate, expireDate, subjectDN, subjectPublicKeyInfo);

        ContentSigner contentSigner = null;
        try {
            contentSigner = new JcaContentSignerBuilder(alg).build(priKey);
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        }

        X509CertificateHolder holder = builder.build(contentSigner);
        X509Certificate certificate = null;
        try {
            certificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        CertAndKey certAndKey = new CertAndKey();
        certAndKey.setX509Certificate(certificate);
        certAndKey.setPrivateKey(priKey);
        certAndKey.setPublicKey(pubKey);
        return certAndKey;
    }
}
