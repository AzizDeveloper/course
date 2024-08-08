package dev.aziz.course;

import dev.aziz.course.config.PasswordConfig;
import dev.aziz.course.entities.Course;
import dev.aziz.course.entities.Role;
import dev.aziz.course.entities.Status;
import dev.aziz.course.entities.User;
import dev.aziz.course.repositories.CourseRepository;
import dev.aziz.course.repositories.RoleRepository;
import dev.aziz.course.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableAsync
public class CourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CourseRepository courseRepository,
            PasswordConfig passwordConfig) {
        return args -> {
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .build();
            Role studentRole = Role.builder()
                    .name("STUDENT")
                    .build();
            Role pupilRole = Role.builder()
                    .name("PUPIL")
                    .build();
            Role teacherRole = Role.builder()
                    .name("TEACHER")
                    .build();
            Role parentRole = Role.builder()
                    .name("PARENT")
                    .build();
            roleRepository.saveAll(List.of(adminRole, studentRole, pupilRole, teacherRole, parentRole));

            Course javaCourse = Course.builder()
                    .title("Java Advanced")
                    .description("Advanced learning of java language")
                    .photoLink("https://media.geeksforgeeks.org/wp-content/uploads/20230823152056/What-is-Advance-JAVA.png")
                    .build();
            courseRepository.save(javaCourse);

            Course frontCourse = Course.builder()
                    .title("Front end Advanced")
                    .description("Advanced learning of front end")
                    .photoLink("https://media.geeksforgeeks.org/wp-content/uploads/20230823152056/What-is-Advance-JAVA.png")
                    .build();
            courseRepository.saveAll(List.of(javaCourse, frontCourse));

            User admin = User.builder()
                    .email("aziz@gmail.com")
                    .firstName("Aziz")
                    .password(passwordConfig.passwordEncoder().encode("asdasd"))
                    .phoneNumber("+998913047777")
                    .status(Status.ENABLED)
                    .roles(Set.of(adminRole, studentRole))
                    .savedCourses(Set.of(javaCourse))
                    .build();

            User student = User.builder()
                    .email("bob@gmail.com")
                    .firstName("Bob")
                    .password(passwordConfig.passwordEncoder().encode("asdasd"))
                    .phoneNumber("+998915024444")
                    .status(Status.ENABLED)
                    .roles(Set.of(studentRole))
                    .savedCourses(Set.of(frontCourse))
                    .build();
            userRepository.saveAll(List.of(admin, student));
        };
    }
}
