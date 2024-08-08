package dev.aziz.course.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SignUpDto {

    private String firstName;
    private String phoneNumber;
    private String email;
    private String role;
    private char[] password;
    private char[] confirmPassword;

}
