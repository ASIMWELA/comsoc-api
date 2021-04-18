package com.comsoc.api.entity;

import com.comsoc.api.Enum.EPosition;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "positions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Position extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 50)
    EPosition name;
    @OneToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnore
    @JoinColumn(name="member_id", referencedColumnName = "id", unique = true)
    Member member;

}
