package com.comsoc.api.payload;

import com.comsoc.api.entity.Member;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
public class Members {
    List<Member> members;
}
