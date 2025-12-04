package com.train.security.security;

import com.train.security.dto.common.UserDetailImpl;
import com.train.security.service.UserService;
import com.train.security.service.extend.RedisTokenService;
import com.train.security.utils.JwtUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(
            @Nonnull
            HttpServletRequest request,
            @Nonnull
            HttpServletResponse response,
            @Nonnull
            FilterChain filterChain)
            throws ServletException, IOException {

        if(request.getServletPath().contains("api/v1/auth") && !request.getServletPath().contains("api/v1/auth/logout")){
            filterChain.doFilter(request,response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        final String token;
        final String username;
        final String bearerPrefix = "Bearer ";

        if(authHeader == null || !authHeader.startsWith(bearerPrefix)){
            filterChain.doFilter(request,response);
            return;
        }

        token = authHeader.substring(bearerPrefix.length());

        if(redisTokenService.isBlackListAccessToken(token)){
            filterChain.doFilter(request,response);
            return;
        }

        username = this.jwtUtil.getUsernameFromToken(token);

        final String storedToken = redisTokenService.getAccessToken(username);
        if(storedToken == null || !storedToken.equals(token)){
            filterChain.doFilter(request,response);
            return;
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            final UserDetailImpl userDetail = (UserDetailImpl) this.userService.loadUserByUsername(username);
            if(this.jwtUtil.validateToken(token, userDetail)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetail,
                        null,
                        userDetail.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request,response);
        }
    }
}
