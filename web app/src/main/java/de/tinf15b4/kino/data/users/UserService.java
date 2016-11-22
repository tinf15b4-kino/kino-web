package de.tinf15b4.kino.data.users;

import java.util.List;

import de.tinf15b4.kino.data.User;

public interface UserService {

    List<User> findAll();

    User findById(long id);

    User findByName(String username);

    User findByEmail(String email);

}
