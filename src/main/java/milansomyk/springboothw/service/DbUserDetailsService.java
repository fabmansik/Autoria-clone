package milansomyk.springboothw.service;

import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null){
            throw new UsernameNotFoundException(username);
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole()).build();
    }
}
