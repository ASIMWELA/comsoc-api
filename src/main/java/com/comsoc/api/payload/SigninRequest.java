package com.comsoc.api.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SigninRequest
{
    @NotEmpty(message = "Registration number cannot be empty")
    String regNumber;

    @NotEmpty(message = "password cannot be empty")
    String password;

}
