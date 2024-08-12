package com.example.shoppingmall.service.user;

import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUserid(userid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 디버깅 로그 추가
            log.debug("Checking authentication for user: " + user.getUserid() + ", Role: " + user.getRole().name());

            // 여기서 권한을 올바르게 매핑합니다.
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserid())
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(authority))
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with userid: " + userid);
        }
    }
}
