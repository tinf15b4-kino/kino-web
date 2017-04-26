package de.tinf15b4.kino.data.users;

import org.springframework.data.jpa.repository.JpaRepository;

import de.tinf15b4.kino.data.users.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByName(String username);

}
