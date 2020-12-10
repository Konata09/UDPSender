package org.konata.udpsender.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtils {
    private static final String jwtKey = "sd*ust#konata&2O20AMDYESAPPLEM1SHANDONGUNIVERSITYOFSCIENCEANDTECNOLOGY";

    public static Map verifyJwt(String token) {
        try {
            Claims jwtBody = Jwts.parserBuilder().setSigningKey(jwtKey.getBytes()).build().parseClaimsJws(token).getBody();
            String username = jwtBody.get("username", String.class);
            Integer role = jwtBody.get("role", Integer.class);
            Date expiration = jwtBody.getExpiration();
            Map map = new HashMap<>();
            map.put("exp", expiration);
            map.put("username", username);
            map.put("role", role);
            return map;
        } catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
    }
}
