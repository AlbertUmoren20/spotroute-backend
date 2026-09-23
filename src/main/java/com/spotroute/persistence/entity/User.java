package com.spotroute.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spotroute.core.enums.Gender;
import com.spotroute.core.enums.Role;
import com.spotroute.core.enums.Source;
import com.spotroute.core.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    private LocalDate dateOfBirth;

    private String profilePicture;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

//    @Column(nullable = false)
//    private String profilePicture;

    @Column(nullable = false)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    private Source source;
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Role role;

    @JsonIgnore
    @Size(max = 255)
    @Column(name = "password_reset_str")
    private String passwordResetStr;

    @JsonIgnore
    @Column(name = "password_reset_expiry_date")
    private Date passwordResetExpiryDate;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Driver-only profile (null for regular users)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private DriverProfile driverProfile;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Booking> bookings;

}
