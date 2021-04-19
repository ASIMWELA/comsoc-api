package com.comsoc.api.payload;

import com.comsoc.api.entity.Member;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PagedResponse {
    List<Member> membersList;
    PageMetadata pageMetadata;
}
