package org.flickit.assessment.data.jpa.users.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "account_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "bio", length = 400)
    private String bio;

    @Column(name = "linkedin", length = 200)
    private String linkedin;

    @Column(name = "picture", length = 100)
    private String picture;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_superuser", nullable = false)
    private Boolean isSuperUser;

    @Column(name = "is_staff", nullable = false)
    private Boolean isStaff;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "current_space_id")
    private Long currentSpaceId;

    @Column(name = "default_space_id")
    private Long defaultSpaceId;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String NAME = "displayName";
    }
}
