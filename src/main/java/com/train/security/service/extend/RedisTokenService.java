package com.train.security.service.extend;

import com.train.security.service.AbsRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisTokenService extends AbsRedisService {

    @Value("${app.jwt.expiration}")
    private long jwtExp;

    @Value("${app.jwt.refreshExpiration}")
    private long refreshExp;

    private final String ACCESS_TOKEN_PREFIX_KEY = "access_token:user:";
    private final String REFRESH_TOKEN_PREFIX_KEY = "refresh_token:user:";

    private final String BLACKLIST_ACCESS_TOKEN_PREFIX_KEY = "blacklist:access_token:";
    private final String BLACKLIST_REFRESH_TOKEN_PREFIX_KEY = "blacklist:refresh_token:";

    public RedisTokenService(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    public void setAccessToken(String email, String token) {
        super.setWithExp(this.ACCESS_TOKEN_PREFIX_KEY + email,
                token,
                jwtExp,
                TimeUnit.MILLISECONDS
        );
    }

    public void setRefreshToken(String email, String token) {
        super.setWithExp(this.REFRESH_TOKEN_PREFIX_KEY + email,
                token,
                refreshExp,
                TimeUnit.MILLISECONDS
        );
    }

    public String getAccessToken(String email) {
        return (String) super.get(this.ACCESS_TOKEN_PREFIX_KEY + email);
    }

    public String getRefreshToken(String email) {
        return (String) super.get(this.REFRESH_TOKEN_PREFIX_KEY + email);
    }

    public void removeAccessToken(String email) {
        final String token = getAccessToken(email);
        if (super.delete(this.ACCESS_TOKEN_PREFIX_KEY + email)) {
            log.info("Remove access token success");
        }

        if(token != null){
            super.setWithExp(
                    this.BLACKLIST_ACCESS_TOKEN_PREFIX_KEY + token,
                    "blacklist",
                    jwtExp,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void removeAllToken(String email) {
        final String accessToken = getAccessToken(email);
        final String refreshToken = getRefreshToken(email);

        if (super.delete(this.ACCESS_TOKEN_PREFIX_KEY + email)) {
            log.info("Remove access token success in all token method");
        }

        if (super.delete(this.REFRESH_TOKEN_PREFIX_KEY + email)) {
            log.info("Remove refresh token success in all token method");
        }

        if(accessToken != null){
            super.setWithExp(
                    this.BLACKLIST_ACCESS_TOKEN_PREFIX_KEY + accessToken,
                    "blacklist",
                    jwtExp,
                    TimeUnit.MILLISECONDS
            );
        }

        if(refreshToken != null){
            super.setWithExp(
                    this.BLACKLIST_REFRESH_TOKEN_PREFIX_KEY + refreshToken,
                    "blacklist",
                    refreshExp,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public boolean isBlackListAccessToken(String token) {
        return super.isKeyExist(this.BLACKLIST_ACCESS_TOKEN_PREFIX_KEY + token);
    }

    public boolean isBlackListRefreshToken(String token) {
        return super.isKeyExist(this.BLACKLIST_REFRESH_TOKEN_PREFIX_KEY + token);
    }
}
