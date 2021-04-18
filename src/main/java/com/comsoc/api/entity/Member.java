package com.comsoc.api.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@ToString(exclude = "articles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "members")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "regNumber")
public class Member extends BaseEntity {
    @Column(name = "first_name", length = 50, nullable = false)
    String firstName;
    @Column(name = "last_name", length = 50, nullable = false)
    String lastName;
    @Column(length = 70, nullable = false, unique = true)
    String email;
    //accommodate bcrypt encoder
    @Column(length = 100, nullable = false)
    @JsonIgnore
    String password;
    @Column(name = "reg_number", length = 80, nullable = false, unique = true)
    String regNumber;
    @Column(name = "last_login", length = 150)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate lastLogin;
    @Column(name = "is_disabled", nullable = false)
    boolean isDisabled;
    @Column(name="year", nullable = false, length = 2)
    int year;
    @OneToMany(mappedBy = "member")
    @LazyCollection(LazyCollectionOption.FALSE)
//    @OrderColumn(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<Article> articles = new ArrayList<>();
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
   // @OrderColumn(name = "id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JoinTable(
            name="user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    List<Role> roles ;
    @OneToOne(mappedBy = "member")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Position position;


    public Member(String firstName, String lastName, String email, String password, String regNumber, int year) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.regNumber = regNumber;
        this.year = year;
    }
}
