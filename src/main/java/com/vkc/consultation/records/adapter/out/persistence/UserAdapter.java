package com.vkc.consultation.records.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.vkc.consultation.records.adapter.out.persistence.mapper.UserMapper;
import com.vkc.consultation.records.application.domain.model.User;
import com.vkc.consultation.records.application.port.out.UserPort;

@Component
public class UserAdapter implements UserPort {

    private final UserRepository userRepository;

    public UserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        return UserMapper.toDomain(userRepository.save(UserMapper.toDocument(user)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
