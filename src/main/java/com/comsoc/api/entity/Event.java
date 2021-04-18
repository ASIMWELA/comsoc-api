package com.comsoc.api.entity;


import com.comsoc.api.Enum.EEventStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "events")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
public class Event extends BaseEntity {
    @Column(nullable = false, length = 10, unique = true)
    String uid;
    @Column(nullable = false)
    LocalDate eventDate;
    @Column(nullable = false, length = 200)
    String location;
    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 15, nullable = false)
    EEventStatus status;
    @Lob
    @Column( nullable = false)
    String agenda;

}
