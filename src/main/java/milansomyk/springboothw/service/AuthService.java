package milansomyk.springboothw.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.requests.RefreshRequest;
import milansomyk.springboothw.dto.requests.SignInRequest;
import milansomyk.springboothw.dto.response.JwtResponse;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.UserRepository;
import milansomyk.springboothw.service.entityServices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Data
@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DbUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseContainer login(SignInRequest signInRequest){
        ResponseContainer responseContainer = new ResponseContainer();
        try {
            Authentication authentication = UsernamePasswordAuthenticationToken
                    .unauthenticated(signInRequest.getUsername(), signInRequest.getPassword());
            authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(signInRequest.getUsername());
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        String username = userDetails.getUsername();
        User user;
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(user)){
            log.error("username not found");
            return responseContainer.setErrorMessageAndStatusCode("username not found",HttpStatus.BAD_REQUEST.value());
        }
        if (!user.isEnabled()){
            log.error("your account is banned");
            return responseContainer.setErrorMessageAndStatusCode("your account is banned",HttpStatus.FORBIDDEN.value());
        }
        String token;
        try {
            token = jwtService.generateToken(userDetails);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        String refresh;
        try {
            refresh = jwtService.generateRefreshToken(userDetails);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult(new JwtResponse(token, refresh));
        return responseContainer;
    }
    public ResponseContainer refresh(RefreshRequest refreshRequest){
        ResponseContainer responseContainer = new ResponseContainer();
        if(!StringUtils.hasText(refreshRequest.getRefresh())){
            log.error("refresh is null");
            return responseContainer.setErrorMessageAndStatusCode("refresh is null",HttpStatus.BAD_REQUEST.value());
        }
        String refreshToken = refreshRequest.getRefresh();
        if (jwtService.isTokenExpired(refreshToken)){
            log.error("refresh token expired");
            return responseContainer.setErrorMessageAndStatusCode( "refresh token expired",HttpStatus.UNAUTHORIZED.value());
        }
        String username;
        try {
            username = jwtService.extractUsername(refreshToken);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(!StringUtils.hasText(username)){
            log.error("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(userDetails)){
            log.error("user details are null");
            return responseContainer.setErrorMessageAndStatusCode("user details are null",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        String access;
        try {
             access = jwtService.generateToken(userDetails);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        boolean isNeedNew;
        try {
            isNeedNew = !(jwtService.extractDuration(refreshToken).toHours() < 12);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (!isNeedNew){
            String newRefresh;
            try {
                newRefresh = jwtService.generateRefreshToken(userDetails);
            } catch (Exception e){
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            responseContainer.setSuccessResult(new JwtResponse(access, newRefresh));
            return responseContainer;
        }
        responseContainer.setSuccessResult(new JwtResponse(access, refreshToken));
        return responseContainer;
    }
}
