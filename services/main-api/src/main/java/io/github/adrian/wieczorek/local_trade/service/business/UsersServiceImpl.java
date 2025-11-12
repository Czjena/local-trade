package io.github.adrian.wieczorek.local_trade.service.business;

import io.github.adrian.wieczorek.local_trade.dto.UpdateUserDto;
import io.github.adrian.wieczorek.local_trade.dto.UserResponseDto;
import io.github.adrian.wieczorek.local_trade.exceptions.UserNotFoundException;
import io.github.adrian.wieczorek.local_trade.mappers.UserMapper;
import io.github.adrian.wieczorek.local_trade.model.UsersEntity;
import io.github.adrian.wieczorek.local_trade.repository.UsersRepository;


import io.github.adrian.wieczorek.local_trade.service.infrastructure.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    @Transactional(readOnly = true)
    public List<UsersEntity> allUsers() {
        return usersRepository.findAll();
    }
    @Transactional
    @Override
    public UserResponseDto updateCurrentUser(UpdateUserDto dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsersEntity currentUser = (UsersEntity) authentication.getPrincipal();
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


