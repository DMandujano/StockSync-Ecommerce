package com.stockSync.backend.user.service;

import com.stockSync.backend.user.model.User;
import com.stockSync.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getMyInvitedUsers() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByOwnerId(currentUser.getId());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
