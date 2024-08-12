package com.example.shoppingmall.controller.user;

import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.security.JwtTokenProvider;
import com.example.shoppingmall.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append("\n"));
            return ResponseEntity.badRequest().body(errorMsg.toString());
        }
        if (userService.checkEmailExists(user.getEmail()) || userService.checkUseridExists(user.getUserid()) || userService.checkNicknameExists(user.getNickname())) {
            return ResponseEntity.badRequest().body("User already exists with this email, userid, or nickname.");
        }
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully. Please check your email for verification.");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean isVerified = userService.verifyEmail(token);
        if (isVerified) {
            return ResponseEntity.ok("Email verification successful. You can now log in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData, HttpServletResponse response) {
        String userid = loginData.get("userid");
        String password = loginData.get("password");

        Optional<User> userOpt = userService.findByUserid(userid);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            String token = tokenProvider.generateToken(userid);

            // JWT 토큰을 쿠키에 저장
            Cookie cookie = new Cookie("JWT", token);
            cookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
            cookie.setSecure(false);   // HTTPS 연결에서만 전송 (로컬 개발 환경에서는 false)
            cookie.setPath("/");       // 애플리케이션 전체에 쿠키를 사용
            cookie.setMaxAge(60 * 60); // 쿠키의 만료 시간 설정 (1시간)
            response.addCookie(cookie);

            // SameSite=None 설정 추가 (만약 필요할 경우)
            response.setHeader("Set-Cookie", "JWT=" + token + "; HttpOnly; Secure; SameSite=Lax; Path=/");

            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.badRequest().body("Invalid userid or password.");
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        // JWT 쿠키에서 가져오기
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("JWT")) {
                    token = cookie.getValue();
                }
            }
        }

        if (token != null && tokenProvider.validateToken(token)) {
            String userid = tokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userid);
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }


    @GetMapping("/find-id")
    public ResponseEntity<String> findUsername(@RequestParam String email, @RequestParam String fullName) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getFullName().equals(fullName)) {
            return ResponseEntity.ok(userOpt.get().getUserid());
        }
        return ResponseEntity.badRequest().body("User not found.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            // Password reset logic here
            return ResponseEntity.ok("Password reset email sent.");
        }
        return ResponseEntity.badRequest().body("User not found.");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user, @RequestParam String currentPassword) {
        Optional<User> userOpt = userService.findByUserid(user.getUserid());
        if (userOpt.isPresent() && passwordEncoder.matches(currentPassword, userOpt.get().getPassword())) {
            userService.updateUser(user);
            return ResponseEntity.ok("User updated successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid password.");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String userid, @RequestParam String password) {
        Optional<User> userOpt = userService.findByUserid(userid);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            userService.deleteUser(userOpt.get().getId());
            return ResponseEntity.ok("User deleted successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid password.");
    }
}
