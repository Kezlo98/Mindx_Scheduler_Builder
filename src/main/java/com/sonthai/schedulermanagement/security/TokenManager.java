package com.sonthai.schedulermanagement.security;

import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TokenManager {

    private final String secret =
            "ChN3IjXwtgDhWQmOdpX2pKemAl5sUXYmjSq7Vrh69olhfQ2n9iOCeDyFw6yT7oY0zKiGr2XHwO2nflrBgPH8bTr4mRWhFIZXMnIwYzb7jA9oEJWzOCE3uUMKgwO2R7tDwMWAtbSRXckmExVuAW4tKfzZcNdim1E57Wwh6ncPhi48JDyzl6zPAsyyofTFNKqNz2fkBtmnLWlz0J3KWj1X1w7oT2V6HDyAhFlCc1ZA";

    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    private final Cache cache;

    public TokenManager(CacheManager cacheManager) {
        String cacheName = "oauthToken";
        cache = cacheManager.getCache(cacheName);
    }

    public Authentication get(String token){
        if(isValidate(token)){
            Cache.ValueWrapper valueWrapper = cache.get(token);
            if(valueWrapper == null){
                return null;
            }
            return (OAuth2AuthenticationToken) valueWrapper.get();
        } else {
            cache.evict(token);
            return null;
        }
    }

    private Boolean isValidate(String token){
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
            jwtParser.parse(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public void set(String token, Authentication authentication){
        cache.put(token,authentication);
    }

    public String generate(OAuth2AuthenticationToken authentication){

        String authorities = CollectionUtils.isEmpty(authentication.getAuthorities()) ? "" : authentication.getAuthorities().stream().map(Objects::toString).collect(Collectors.joining(",")) ;
        Date expiration = new  Date(System.currentTimeMillis() + (60 * 60 * 1000));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities",authorities)
                .claim("name", authentication.getPrincipal().getAttribute("name"))
                .claim("email", getUserName(authentication.getPrincipal().getAttributes(), authentication.getAuthorizedClientRegistrationId()))
                .signWith(key)
                .setExpiration(expiration)
                .compact();
    }

    public String getUserName(Map<String, Object> attributes, String registrationId) {
        if (RegistrationEnum.value(registrationId) == RegistrationEnum.GITHUB) {
            return  (String) attributes.get("login");
        }
        return (String) attributes.get("email");
    }

    public String getUserNameFromAuthentication(Authentication authentication){
        if(authentication instanceof OAuth2AuthenticationToken){
            return getUserName(((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes(), ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());
        }

        return authentication.getName();
    }

    public String getRegistrationId(Authentication authentication){
        if (authentication instanceof OAuth2AuthenticationToken){
            return ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        }

        return RegistrationEnum.BASIC.name();
    }
}
