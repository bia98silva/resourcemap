package com.resourcemap.config;

import com.resourcemap.model.User;
import com.resourcemap.model.UserRole;
import com.resourcemap.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2SuccessHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        if (userRepository.findByEmail(email).isEmpty()) {
    
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(UserRole.DONOR); 
            newUser.setCreatedAt(LocalDateTime.now());
            userRepository.save(newUser);
        }

        getRedirectStrategy().sendRedirect(request, response, "/dashboard");
    }
}
