package io.github.adrian.wieczorek.local_trade.service.user.service;


import io.github.adrian.wieczorek.local_trade.service.user.dto.LoginDto;
import io.github.adrian.wieczorek.local_trade.service.user.dto.RegisterUsersDto;
import io.github.adrian.wieczorek.local_trade.service.user.UsersEntity;
import io.github.adrian.wieczorek.local_trade.service.user.UsersRepository;
import io.github.adrian.wieczorek.local_trade.service.user.facade.UserEventFacade;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserEventFacade userEventFacade;



    @Override
    @Transactional
    public UsersEntity signup(RegisterUsersDto dto) {
        UsersEntity user = new UsersEntity();
                user.setName(dto.getName());
                user.setEmail(dto.getEmail());
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        userEventFacade.publishUserRegistered(user);
        return user;
    }

    @Override
    @Transactional
    public UsersEntity authenticate(LoginDto dto) {
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
        UsersEntity currentUser = (UsersEntity) auth.getPrincipal();
        List<String> listOfRoles = new ArrayList<>();
        currentUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).forEach(listOfRoles::add);
        return listOfRoles;
    }
}