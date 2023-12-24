package com.library.server.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtility {
    private static final String SECRET_KEY = "q8vEwpOC~k5SGpWtQHv5{B2w1EBp#MbnBivSxftgbe8Njw%3KiUP8lUe~884cqL~{Mr9IYsQtql$SwARO|W~?0t8PLyL~}p$PQS";

    public static String generate(long id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 864_000_000L);

        return Jwts.builder()
                .setSubject(Long.toString(id))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Long parse(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(jwt)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }
}
