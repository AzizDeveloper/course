package dev.aziz.course.mappers;

import dev.aziz.course.dtos.SignUpDto;
import dev.aziz.course.dtos.UserDto;
import dev.aziz.course.dtos.UserSummaryDto;
import dev.aziz.course.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        } else {
            UserDto.UserDtoBuilder userDto = UserDto.builder();
            userDto.id(user.getId());
            userDto.firstName(user.getFirstName());
            userDto.email(user.getEmail());
            userDto.phoneNumber(user.getPhoneNumber());
            userDto.status(user.getStatus());
            userDto.roles(user.getRoles());
            userDto.savedCourses(user.getSavedCourses());
            return userDto.build();
        }
    }

    public User signUpToUser(SignUpDto signUpDto) {
        if (signUpDto == null) {
            return null;
        } else {
            User.UserBuilder user = User.builder();
            user.firstName(signUpDto.getFirstName());
            user.email(signUpDto.getEmail());
            user.phoneNumber(signUpDto.getPhoneNumber());
            return user.build();
        }
    }

    public UserSummaryDto toUserSummaryDto(User user) {
        if (user == null) {
            return null;
        } else {
            UserSummaryDto.UserSummaryDtoBuilder userSummaryDto = UserSummaryDto.builder();
            userSummaryDto.id(user.getId());
            userSummaryDto.firstName(user.getFirstName());
            userSummaryDto.email(user.getEmail());
            userSummaryDto.phoneNumber(user.getPhoneNumber());
            userSummaryDto.status(user.getStatus());
            userSummaryDto.roles(user.getRoles());
            userSummaryDto.savedCourses(user.getSavedCourses());
            return userSummaryDto.build();
        }
    }

    public List<UserDto> usersToUserDtos(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    public List<UserSummaryDto> usersToUserSummaryDtos(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toUserSummaryDto)
                .collect(Collectors.toList());
    }

}
