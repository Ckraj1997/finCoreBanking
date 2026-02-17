package mca.fincorebanking.service.impl;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mca.fincorebanking.entity.Role;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.repository.UserRepository;
import mca.fincorebanking.service.NotificationService;
import mca.fincorebanking.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public UserServiceImpl(
            NotificationService notificationService, UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
       
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public long countByRole(String role) {
        return userRepository.countByRole(Role.valueOf(role));

    }

    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(false);
        userRepository.save(user);

        
        notificationService.notify(user, "Your account has been blocked by admin");
    }

    @Override
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(true);
        userRepository.save(user);

        
        notificationService.notify(user, "Your account has been unblocked by admin");
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
