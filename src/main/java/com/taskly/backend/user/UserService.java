package com.taskly.backend.user;

import com.taskly.backend.user.dto.CreateUserRequestDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(CreateUserRequestDto userDto){
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(userDto.getPasswordHash());

        return userRepository.save(user);
    }
}
