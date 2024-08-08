package dev.aziz.course.dtos;

import dev.aziz.course.entities.Course;
import dev.aziz.course.entities.Role;
import dev.aziz.course.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {

    private Long id;
    private String firstName;
    private String phoneNumber;
    private String email;
    private Status status;
    private String token;
    private Set<Role> roles;
    private Set<Course> savedCourses;
}
