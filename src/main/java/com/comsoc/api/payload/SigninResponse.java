package com.comsoc.api.payload;

import com.comsoc.api.entity.Member;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class SigninResponse {
    TokenPayload tokenPayload;
    Member userData;

}
