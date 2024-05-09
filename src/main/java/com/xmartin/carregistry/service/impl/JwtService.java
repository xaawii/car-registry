package com.xmartin.carregistry.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    //recuperamos de properties la secret key de la aplicación
    @Value("${token.secret.key}")
    String jwtSecretKey;


    //recuperamos de properties el tiempo de expiración en ms
    @Value("${token.expirationms}")
    Long jwtExpirationMs;


    //extrae el nombre del usuario
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    //genera el token pasando los detalles del usuario
    public String generateToken(UserDetails userDetails) {
        //llama al otro método sobrecargado para generar el token
        return generateToken(new HashMap<>(), userDetails);
    }


    //comprueba que el token es valido, pasando el token y los detalles del usuario y que el token no esté caducado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        //obtiene el nombre del usuario del token
        final String userName = extractUserName(token);
        //comprueba que sea igual el del token que el de los detalles del usuario
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    /*
    extrae los claims pasandole el token y una función de la clase Claims
    devuelve tipo genérico T para usar este método para llamar a distintas
    funciones de Claims como getSubject o getExpiration
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        //obtiene todos los claims del token
        final Claims claims = extractAllClaims(token);
        //usa la función con el objeto claims
        return claimsResolvers.apply(claims);
    }


    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /*
    generamos el token con jwts builder usando extraclaims (si hay) y los detalles del usuario
    también la fecha en la qeu se genera y la de expiración, y firmamos con la secret key.
     */

    /*
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    */


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /*
        private Claims extractAllClaims(String token) {
            return Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getPayload();
        }

        //key está deprecated para el verifyWith, cambio a secretkey que también le sirve a signWith
        private SecretKey getSigningKey() {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        }

        */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
