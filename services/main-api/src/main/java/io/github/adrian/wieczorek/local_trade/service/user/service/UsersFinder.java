package io.github.adrian.wieczorek.local_trade.service.user.service;

import io.github.adrian.wieczorek.local_trade.service.user.UsersEntity;
import io.github.adrian.wieczorek.local_trade.service.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsersFinder {

    private final UsersRepository usersRepository;

    public List<UsersEntity> allUsers() {
        return usersRepository.findAll();
    }

}
