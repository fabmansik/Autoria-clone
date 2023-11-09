package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.requests.RefreshRequest;
import milansomyk.springboothw.dto.requests.SignInRequest;
import milansomyk.springboothw.dto.response.JwtResponse;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.exceptions.UserBanedException;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Data
@Service
public class LoginRegisterRefreshService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DbUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public UserResponse register(UserDto userDto){
        User user = userMapper.fromDto(userDto);
        try{
            userService.isUsernameAlreadyExists(user.getUsername());
            userService.isEmailAlreadyExists(user.getEmail());
            userService.isPhoneNumberAlreadyUsed(user.getPhone());
            }
        catch (IllegalArgumentException e){
            return new UserResponse(null,e.getMessage());
        }

        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        User savedUser = userRepository.save(user.setPremium(false).setEnabled(true).setRole(Role.SELLER.name()));
        return new UserResponse(userMapper.toDto(savedUser),null);
    }
    public JwtResponse login(SignInRequest signInRequest){
        try {
            Authentication authentication = UsernamePasswordAuthenticationToken
                    .unauthenticated(signInRequest.getUsername(), signInRequest.getPassword());
            authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e){
            return new JwtResponse(null,null, e.getMessage());
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(signInRequest.getUsername());
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);
        try{
            if (!user.isEnabled()){
                throw new UserBanedException("Your account is banned");
            }
        }catch (UserBanedException e){
            return new JwtResponse(null,null,e.getMessage());
        }
        String token = jwtService.generateToken(userDetails);
        String refresh = jwtService.generateRefreshToken(userDetails);
        return new JwtResponse(token, refresh, null);
    }
    public JwtResponse refresh(RefreshRequest refreshRequest){
        String refreshToken = refreshRequest.getRefresh();
        if (jwtService.isTokenExpired(refreshToken)){
            return new JwtResponse(null, null, "refresh token expired");
        }
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String access = jwtService.generateToken(userDetails);
        if (jwtService.extractDuration(refreshToken).toHours()<12){
            String newRefresh = jwtService.generateRefreshToken(userDetails);
            return new JwtResponse(access, newRefresh, null);
        }
        return new JwtResponse(access, refreshToken, null);
    }
}
