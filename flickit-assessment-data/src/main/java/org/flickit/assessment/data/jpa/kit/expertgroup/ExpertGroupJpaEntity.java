package org.flickit.assessment.data.jpa.kit.expertgroup;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "baseinfo_expertgroup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExpertGroupJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_expertgroup_id_seq")
    @SequenceGenerator(name = "baseinfo_expertgroup_id_seq", sequenceName = "baseinfo_expertgroup_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String title;

    @Column(name = "bio", length = 200, nullable = false)
    private String bio;

    @Column(name = "about", nullable = false, columnDefinition = "TEXT")
    private String about;

    @Column(name = "picture", length = 100)
    private String picture;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String NAME = "title";
    }
}
