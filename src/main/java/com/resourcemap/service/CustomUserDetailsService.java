package com.resourcemap.service;

import com.resourcemap.model.User;
import com.resourcemap.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== TENTATIVA DE LOGIN ===");
        System.out.println("Email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ Usuário não encontrado: " + email);
                    return new UsernameNotFoundException("Usuário não encontrado: " + email);
                });

        System.out.println("✅ Usuário encontrado:");
        System.out.println("- Nome: " + user.getName());
        System.out.println("- Role: " + user.getRole());
        System.out.println("- ID: " + user.getId());
        System.out.println("- Tem senha: " + (user.getPassword() != null && !user.getPassword().isEmpty()));

        if (user.getPassword() != null) {
            System.out.println("- Tamanho da senha criptografada: " + user.getPassword().length());
            System.out.println("- Começa com $2a$: " + user.getPassword().startsWith("$2a$"));
        }

        // Verificar se é usuário OAuth (sem senha)
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            System.out.println("❌ Usuário OAuth tentando login com senha");
            throw new UsernameNotFoundException("Usuário OAuth não pode fazer login com senha");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // CRUCIAL: usar a senha criptografada como está
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                getAuthorities(user)
        );

        System.out.println("✅ UserDetails criado com sucesso");
        System.out.println("=== FIM TENTATIVA LOGIN ===");

        return userDetails;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String roleName = "ROLE_" + user.getRole().name();
        System.out.println("- Autoridade: " + roleName);
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}