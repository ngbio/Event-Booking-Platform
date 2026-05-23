package com.group3.utils;

import com.group3.exceptions.BusinessException;
import com.group3.exceptions.TokenNotValidException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
/*  
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author THUAN
 */
public class JwtUtils {
    private static final String SECRET = "12345678901234567890123456789012"; // 32 ký tự (AES key)
    private static final long EXPIRATION_MS = 86400000; // 1 ngày

    public static String generateToken(String email) {
        try {
        JWSSigner signer = new MACSigner(SECRET);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        signedJWT.sign(signer);

        return signedJWT.serialize();}
        catch (JOSEException ex){
            throw new BusinessException("Lỗi tạo token");
        }
    }

    public static String validateTokenAndGetUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET);

            if (signedJWT.verify(verifier)) {
                Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
                if (expiration.after(new Date())) {
                    return signedJWT.getJWTClaimsSet().getSubject();
                }
                throw new TokenNotValidException("Token đã hết hạn!");
            }
            throw new TokenNotValidException("Token không hợp lệ");
        } catch (TokenNotValidException | JOSEException | ParseException e) {
            throw new TokenNotValidException("Token không đúng định dạng");
        }
    }
}
