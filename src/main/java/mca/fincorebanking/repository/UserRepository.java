package mca.fincorebanking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mca.fincorebanking.entity.User;
import mca.fincorebanking.entity.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    long countByRole(Role role);

}
