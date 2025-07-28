package com.f4ken0name.github.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JweHelper {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public JweHelper(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String generateEncryptedToken(String username, String role) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("role", role)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 86400000))
                    .build();

            JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                    .contentType("JWT")
                    .build();

            EncryptedJWT encryptedJWT = new EncryptedJWT(header, claimsSet);
            RSAEncrypter encrypter = new RSAEncrypter((java.security.interfaces.RSAPublicKey) publicKey);
            encryptedJWT.encrypt(encrypter);

            String token = encryptedJWT.serialize();
            System.out.println("----- GENERATED JWE TOKEN -----");
            System.out.println(token);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate encrypted token", e);
        }
    }


    public JWTClaimsSet parseEncryptedToken(String encryptedToken) {
        try {
            EncryptedJWT encryptedJWT = EncryptedJWT.parse(encryptedToken);
            RSADecrypter decrypter = new RSADecrypter(privateKey);
            encryptedJWT.decrypt(decrypter);
            return encryptedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse encrypted token", e);
        }
    }

    public String getUsernameFromToken(String token) {
        return parseEncryptedToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            JWTClaimsSet claims = parseEncryptedToken(token);
            return claims.getExpirationTime().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        return parseEncryptedToken(token).getClaim("role").toString();
    }
}
