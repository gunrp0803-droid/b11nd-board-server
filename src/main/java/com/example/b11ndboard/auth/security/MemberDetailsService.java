package com.example.b11ndboard.auth.security;

import com.example.b11ndboard.auth.entity.Users;
import com.example.b11ndboard.global.exception.ErrorCode;
import com.example.b11ndboard.global.exception.LoginException;
import com.example.b11ndboard.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new LoginException(ErrorCode.LOGIN_FAILED));

        return new MemberDetails(user);
    }
}
