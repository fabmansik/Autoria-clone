package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.JwtResponse;
import milansomyk.springboothw.dto.requests.RefreshRequest;
import milansomyk.springboothw.dto.requests.SignInRequest;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.service.LoginRegisterRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginRegisterRefreshController {

    @Autowired
    private final LoginRegisterRefreshService loginRegisterRefreshService;
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> signIn(@RequestBody SignInRequest signInRequest){
        return ResponseEntity.ok(loginRegisterRefreshService.login(signInRequest));
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> create(@RequestBody UserDto userDto){
        return ResponseEntity.ok(loginRegisterRefreshService.register(userDto));
    }
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshRequest refreshRequest){
        return ResponseEntity.ok(loginRegisterRefreshService.refresh(refreshRequest));
    }
}
