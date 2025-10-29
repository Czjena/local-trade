package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.UpdateUserDto;
import io.github.czjena.local_trade.dto.UserResponseDto;
import io.github.czjena.local_trade.exceptions.UserNotFoundException;
import io.github.czjena.local_trade.mappers.UserMapper;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;

    }
    @Override
    public List<Users> allUsers() {
        return usersRepository.findAll();
    }
    @Transactional
    @Override
    public UserResponseDto updateCurrentUser(UpdateUserDto dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();
        if (currentUser == null) {
            throw new UserNotFoundException("User not found");
        }
        currentUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        currentUser.setName(dto.getName());
        currentUser.setEmail(dto.getEmail());
        usersRepository.save(currentUser);
        return UserMapper.toDto(currentUser);

    }
}


