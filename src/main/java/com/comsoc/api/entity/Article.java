package com.comsoc.api.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "articles")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
public class Article extends BaseEntity{
    @Column(nullable = false, length = 10, unique = true)
    String uid;
    @Lob
    @Column(length = 2500, nullable = false)
    String content;
    @Column(nullable = false)
    LocalDate createdAt;
    @ManyToOne(targetEntity = Member.class)
    @JoinColumn(name="member_id")
    private Member member;
}
