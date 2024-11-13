package com.banghyang.user.service;

import com.banghyang.user.dto.SignupRequestDTO;
import com.banghyang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public void signup(SignupRequestDTO signupRequestDTO) {
        userRepo.save(signupRequestDTO.toEntity());
    }

}
