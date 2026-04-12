package com.vkc.consultation.records.application.port.out;

import java.util.Optional;

import com.vkc.consultation.records.application.domain.model.User;

public interface UserPort {
    Optional<User> findByEmail(String email);
    User save(User user);
    boolean existsByEmail(String email);
}
