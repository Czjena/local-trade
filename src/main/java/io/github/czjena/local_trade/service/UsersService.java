package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.LoginDto;
import io.github.czjena.local_trade.dto.RegisterUsersDto;
import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

public class UsersService {
    private final BCryptPasswordEncoder passwordEncoder;

    private final UsersRepository usersRepository;

    public UsersService(BCryptPasswordEncoder passwordEncoder, UsersRepository usersRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
    }

    public Users createUser(RegisterUsersDto dto) {
        Users user = new Users();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return usersRepository.save(user);

    }

    public Optional<Users> loginUser(LoginDto dto) {
        Optional<Users> user = usersRepository.findByEmail(dto.getEmail());
            if(user.isPresent()) {
                if (passwordEncoder.matches(dto.getPassword(), user.get().getPassword())) {
                    return user;
                }
            }
            return Optional.empty();
        }

    public RegisterUsersDto toDto(Users user) {
        RegisterUsersDto dto = new RegisterUsersDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}

