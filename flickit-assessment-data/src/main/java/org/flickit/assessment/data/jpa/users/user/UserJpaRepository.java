package org.flickit.assessment.data.jpa.users.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    @Query("""
        SELECT u.email AS email
        FROM UserJpaEntity u
        WHERE u.id = :userId
        """)
    String findEmailByUserId(@Param(value = "userId") UUID userId);
}
