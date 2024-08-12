package com.example.shoppingmall.service.user;

import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);

        // 이메일 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(24)); // 24시간 유효

        // 사용자 저장
        userRepository.save(user);

        // 이메일 발송
        sendVerificationEmail(user.getEmail(), token);

        return user;
    }

    public boolean verifyEmail(String token) {
        Optional<User> userOptional = userRepository.findByEmailVerificationToken(token);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 토큰이 유효한지 확인 (만료 시간 등 체크)
            if (user.getEmailVerificationTokenExpiry().isAfter(LocalDateTime.now())) {
                user.setEmailVerified(true);
                user.setEmailVerificationToken(null); // 토큰을 null로 설정해 재사용 방지
                user.setEmailVerificationTokenExpiry(null); // 만료 시간도 null로 설정
                userRepository.save(user);
                return true; // 인증 성공
            }
        }

        return false; // 인증 실패
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkUseridExists(String userid) {
        return userRepository.findByUserid(userid).isPresent();
    }

    public boolean checkNicknameExists(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    public Optional<User> findByUserid(String userid) {
        return userRepository.findByUserid(userid);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private void sendVerificationEmail(String email, String token) {
        String subject = "Email Verification";
        String confirmationUrl = "http://localhost:8080/api/users/verify-email?token=" + token;
        String message = "Please verify your email by clicking the link below:\n" + confirmationUrl;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message, true);
            helper.setFrom("khh5345@naver.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            // 이메일 발송 실패 처리 로직
        }
    }
}
