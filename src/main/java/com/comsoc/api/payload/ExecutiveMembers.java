package com.comsoc.api.payload;

import com.comsoc.api.entity.Member;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
public class ExecutiveMembers {
    List<Member> executives;
}
