package com.resourcemap.config;

import com.resourcemap.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Lazy OAuth2SuccessHandler oauth2SuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Permitir acesso público às páginas básicas
                        .requestMatchers("/", "/home", "/login", "/register", "/error").permitAll()
                        // Permitir acesso aos recursos estáticos
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        // Permitir acesso às APIs públicas
                        .requestMatchers("/api/statistics").permitAll()
                        // Proteger APIs que precisam de autenticação
                        .requestMatchers("/api/**").authenticated()
                        // Permitir APIs da IA para usuários autenticados
                        .requestMatchers("/ai-assistant/**").authenticated()
                        // Proteger área administrativa
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Todas as outras requisições precisam de autenticação
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        // Desabilitar CSRF para APIs da IA (requisições AJAX)
                        .ignoringRequestMatchers("/ai-assistant/**", "/api/**")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oauth2SuccessHandler)
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}