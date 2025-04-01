package com.fiscontrolbackend.fiscontrolbackend.security;

import com.fiscontrolbackend.fiscontrolbackend.models.user.UserEntity;
import com.fiscontrolbackend.fiscontrolbackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UserRepository userRepository;

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        UserEntity user = userRepository.findById(userId).orElse(null);

        return user != null && user.getUsername().equals(currentUsername);
    }
}

