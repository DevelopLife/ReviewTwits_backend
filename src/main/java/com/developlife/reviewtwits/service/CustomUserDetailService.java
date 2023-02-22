package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.exception.AccountIdNotFoundException;
import com.developlife.reviewtwits.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author ghdic
 * @since 2023.02.19
 */
@Service
public class CustomUserDetailService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountIdNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
