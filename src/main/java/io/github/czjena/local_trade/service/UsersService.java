package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    public List<Users> allUsers() {
        return usersRepository.findAll();
    }
}
