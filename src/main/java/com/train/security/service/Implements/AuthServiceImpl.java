package com.train.security.service.Implements;

import com.train.security.constants.ResponseMessage;
import com.train.security.dto.common.UserDetailImpl;
import com.train.security.dto.request.LoginReq;
import com.train.security.dto.request.RegisterReq;
import com.train.security.dto.response.AuthRes;
import com.train.security.entity.User;
import com.train.security.exceptions.CommonException;
import com.train.security.repositories.UserRepository;
import com.train.security.service.AuthService;
import com.train.security.service.UserService;
import com.train.security.service.extend.RedisTokenService;
import com.train.security.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenService redisTokenService;
    private final UserService userService;

    @Override
    public AuthRes login(LoginReq req) {
        try {
            final Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()
                    )
            );

            final UserDetailImpl user = (UserDetailImpl) auth.getPrincipal();
            final String accessToken = jwtUtil.generateAccessToken(user);
            final String refreshToken = jwtUtil.generateRefreshToken(user);
            redisTokenService.setAccessToken(user.getEmail(), accessToken);
            redisTokenService.setRefreshToken(user.getEmail(), refreshToken);

            return AuthRes.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    @Override
    public void register(RegisterReq req) {
        if (userRepo.existsByEmailIgnoreCase(req.getEmail())) {
            throw new CommonException(
                    ResponseMessage.ERR_USER_002.name(),
                    ResponseMessage.ERR_USER_002.getMessage(),
                    ResponseMessage.ERR_USER_002.getStatus()
            );
        }
        final String encodedPassword = passwordEncoder.encode(req.getPassword());
        User newUser = User.builder()
                .email(req.getEmail())
                .password(encodedPassword)
                .build();
        userRepo.save(newUser);
    }

    @Override
    public void logout() {
        UserDetailImpl userPrincipal = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrincipal == null){
            throw new CommonException(
                    ResponseMessage.ERR_AUTH_003.name(),
                    ResponseMessage.ERR_AUTH_003.getMessage(),
                    ResponseMessage.ERR_AUTH_003.getStatus()
            );
        }
        redisTokenService.removeAllToken(userPrincipal.getEmail());
    }

    @Override
    public AuthRes refreshToken(String refreshToken) {

        System.out.println("Refresh Token: " + refreshToken);

        if(redisTokenService.isBlackListRefreshToken(refreshToken)){
            log.warn("Refresh token is blacklisted");
            throw new CommonException(
                    ResponseMessage.ERR_AUTH_002.name(),
                    ResponseMessage.ERR_AUTH_002.getMessage(),
                    ResponseMessage.ERR_AUTH_002.getStatus()
            );
        }

        final String username = jwtUtil.getUsernameFromToken(refreshToken);

        final UserDetailImpl userDetail = (UserDetailImpl) userService.loadUserByUsername(username);
        if (username == null || !jwtUtil.validateRefreshToken(refreshToken, userDetail)) {
            log.warn("Invalid refresh token");
            throw new CommonException(
                    ResponseMessage.ERR_AUTH_002.name(),
                    ResponseMessage.ERR_AUTH_002.getMessage(),
                    ResponseMessage.ERR_AUTH_002.getStatus()
            );
        }

        final String storedRefreshToken = redisTokenService.getRefreshToken(userDetail.getEmail());

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            log.warn("Refresh token does not match stored token");
            throw new CommonException(
                    ResponseMessage.ERR_AUTH_002.name(),
                    ResponseMessage.ERR_AUTH_002.getMessage(),
                    ResponseMessage.ERR_AUTH_002.getStatus()
            );
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetail.getEmail(),
                null,
                userDetail.getAuthorities()
        );

        final String newAccessToken = jwtUtil.generateAccessToken(userDetail);

        redisTokenService.removeAccessToken(userDetail.getEmail());
        redisTokenService.setAccessToken(userDetail.getEmail(), newAccessToken);

        return AuthRes.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
