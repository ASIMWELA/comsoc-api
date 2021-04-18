package com.comsoc.api.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class SignupRequest {
    @NotEmpty(message = "first name cannot be empty")
    String firstName;
    @NotEmpty(message = "last name cannot be empty")
    String lastName;
    @NotEmpty(message = "email cannot be empty")
    @Email(message = "Email provided is not valid")
    String email;
    @NotEmpty(message = "password cannot be empty")
    @Size(min=5, max = 15, message = "password must be between 5 and 15 characters")
    String password;
    @NotEmpty(message = "registration number cannot be empty")
    String regNumber;
    @NotEmpty(message = "Year of study cannot be empty")
    @Size(min=1, max = 2, message = "Year of study can only be a single digit")
    int year;
}
