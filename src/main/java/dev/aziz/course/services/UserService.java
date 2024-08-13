package dev.aziz.course.services;

import dev.aziz.course.config.PasswordConfig;
import dev.aziz.course.config.UserAuthProvider;
import dev.aziz.course.dtos.CredentialsDto;
import dev.aziz.course.dtos.SignUpDto;
import dev.aziz.course.dtos.UserDto;
import dev.aziz.course.dtos.UserSummaryDto;
import dev.aziz.course.entities.Confirmation;
import dev.aziz.course.entities.Course;
import dev.aziz.course.entities.Role;
import dev.aziz.course.entities.Status;
import dev.aziz.course.entities.User;
import dev.aziz.course.exceptions.AppException;
import dev.aziz.course.mappers.UserMapper;
import dev.aziz.course.repositories.ConfirmationRepository;
import dev.aziz.course.repositories.CourseRepository;
import dev.aziz.course.repositories.RoleRepository;
import dev.aziz.course.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CourseRepository courseRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final UserAuthProvider userAuthProvider;
    private final PasswordEncoder passwordEncoder;
    private final PasswordConfig passwordConfig;


    @Autowired
    public UserService(@Lazy UserAuthProvider userAuthProvider, UserRepository userRepository, RoleRepository roleRepository, CourseRepository courseRepository, ConfirmationRepository confirmationRepository, EmailService emailService, UserMapper userMapper, PasswordEncoder passwordEncoder, PasswordConfig passwordConfig) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.courseRepository = courseRepository;
        this.confirmationRepository = confirmationRepository;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.userAuthProvider = userAuthProvider;
        this.passwordEncoder = passwordEncoder;
        this.passwordConfig = passwordConfig;
    }

    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public UserDto login(CredentialsDto credentialsDto) {
        log.info("User {} logged in.", credentialsDto.getEmail());
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }

        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto signUpDto) {
        Optional<User> optionalUser = userRepository.findByEmail(signUpDto.getEmail());

        if (!passwordsAreEqual(signUpDto.getPassword(), signUpDto.getConfirmPassword())) {
            throw new AppException("Passwords are not equal", HttpStatus.BAD_REQUEST);
        }

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        Role role = roleRepository.findRoleByName(signUpDto.getRole())
                .orElseThrow(() -> new AppException("Role not found", HttpStatus.NOT_FOUND));
        User user = userMapper.signUpToUser(signUpDto);
        user.setRoles(Set.of(role));
        user.setStatus(Status.DISABLED);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.getPassword())));
        User savedUser = userRepository.save(user);
        log.info("User by login {} has been registered.", savedUser.getEmail());
        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);

        emailService.sendSimpleMailMessage(user.getFirstName(), user.getEmail(), confirmation.getActivationCode());
        return userMapper.toUserDto(user);
    }

    private boolean passwordsAreEqual(char[] password, char[] confirmPassword) {
        if (password.length != confirmPassword.length) {
            return false;
        }
        for (int i = 0; i < password.length; i++) {
            if (password[i] != confirmPassword[i]) {
                return false;
            }
        }
        return true;
    }

    public Boolean verifyActivationCode(String activationCode) {
        Confirmation confirmation = confirmationRepository.findByActivationCode(activationCode);
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        user.setStatus(Status.ENABLED);
        userRepository.save(user);
        confirmationRepository.delete(confirmation);
        return Boolean.TRUE;
    }


    public List<UserSummaryDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserSummaryDto> userDtos = userMapper.usersToUserSummaryDtos(users);
        return userDtos;
    }

    public UserSummaryDto getUserById(Long id, UserDto userDto) {
        boolean isAdmin = userDto.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
        if (id.longValue() != userDto.getId()) {
            if (!isAdmin) {
                throw new AppException("You do not have correct role or you are not this user", HttpStatus.FORBIDDEN);
            }
        }
        return userMapper.toUserSummaryDto(userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found.", HttpStatus.NOT_FOUND)));
    }

    public void deleteUserById(Long id) {
        userRepository.findById(id).orElseThrow(() -> new AppException("User not found.", HttpStatus.NOT_FOUND));
        userRepository.deleteById(id);
    }

    public UserSummaryDto addCourseToCart(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Course course = courseRepository.findCourseById(courseId)
                .orElseThrow(() -> new AppException("Course not found", HttpStatus.NOT_FOUND));
        Set<Course> savedCourses = user.getSavedCourses();
        savedCourses.add(course);
        user.setSavedCourses(savedCourses);
        User savedUser = userRepository.save(user);
        return userMapper.toUserSummaryDto(savedUser);
    }

    public UserSummaryDto deleteCourseFromCart(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Course course = courseRepository.findCourseById(courseId)
                .orElseThrow(() -> new AppException("Course not found", HttpStatus.NOT_FOUND));
        Set<Course> savedCourses = user.getSavedCourses();
        savedCourses.remove(course);
        user.setSavedCourses(savedCourses);
        User savedUser = userRepository.save(user);
        return userMapper.toUserSummaryDto(savedUser);
    }

    public UserSummaryDto forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        Confirmation confirmation = new Confirmation(user);
        String token = userAuthProvider.createToken(email);
        confirmation.setActivationCode(token);
        confirmationRepository.save(confirmation);
        emailService.sendSimpleMailMessageWithSecretCode(user.getFirstName(), user.getEmail(), token);
        return userMapper.toUserSummaryDto(user);
    }


    public UserSummaryDto createNewPassword(String secretCode, String newPassword) {
        userAuthProvider.validateToken(secretCode);
        Long userId = userAuthProvider.getUserId(secretCode);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        user.setPassword(passwordConfig.passwordEncoder().encode(newPassword));
        User save = userRepository.save(user);
        System.out.println("User updated with new password:");
        System.out.println(save);
        Confirmation code = confirmationRepository.findByActivationCode(secretCode);
        confirmationRepository.delete(code);
        return userMapper.toUserSummaryDto(user);
    }
}
