package dev.aziz.course.repositories;

import dev.aziz.course.entities.Course;
import dev.aziz.course.entities.Role;
import dev.aziz.course.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<Role> findRolesById(Long id);

    List<Course> findCoursesById(Long id);

    Optional<User> findByEmailIgnoreCase(String email);

}
