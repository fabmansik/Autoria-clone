package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.response.JwtResponse;
import milansomyk.springboothw.dto.requests.RefreshRequest;
import milansomyk.springboothw.dto.requests.SignInRequest;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ResponseContainer> signIn(@RequestBody SignInRequest signInRequest){
        ResponseContainer responseContainer = authService.login(signInRequest);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseContainer> refresh(@RequestBody RefreshRequest refreshRequest){
        ResponseContainer responseContainer = authService.refresh(refreshRequest);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
}
