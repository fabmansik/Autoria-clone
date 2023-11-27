package milansomyk.springboothw.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.security.SignatureException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.startsWithIgnoreCase(authorization, AUTHORIZATION_HEADER_PREFIX)){
            filterChain.doFilter(request,response);
            return;
        }
        String token = null;
        String username = null;
        ResponseContainer responseContainer = new ResponseContainer();
        try {
            token = authorization.substring(AUTHORIZATION_HEADER_PREFIX.length());
            if (jwtService.isTokenExpired(token)){
                filterChain.doFilter(request,response);
                return;
            }
            username = jwtService.extractUsername(token);



        SecurityContext securityContext = SecurityContextHolder.getContext();
            if (StringUtils.hasText(username) && securityContext.getAuthentication() == null && !jwtService.isRefreshType(token)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities());
                securityContext.setAuthentication(authentication);
            }
        filterChain.doFilter(request,response);
        } catch (JwtException e){
            log.error(e.getMessage());
            responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(responseContainer.getStatusCode());
            OutputStream responseStream = response.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(responseStream, responseContainer);
            responseStream.flush();
        }
    }
}
