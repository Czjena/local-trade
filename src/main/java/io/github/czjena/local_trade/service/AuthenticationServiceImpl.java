package io.github.czjena.local_trade.service;


import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.dto.RegisterUsersDto;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UsersRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(
            UsersRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Users signup(RegisterUsersDto dto) {
        Users user = new Users();
                user.setName(dto.getName());
                user.setEmail(dto.getEmail());
                user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Users authenticate(LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        return userRepository.findByEmail(dto.getEmail())
                .orElseThrow();
    }
    @Override
    @Transactional
    public List<String> getAuthenticatedRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) auth.getPrincipal();
        List<String> listOfRoles = new ArrayList<>();
        currentUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).forEach(listOfRoles::add);
        return listOfRoles;
    }
}