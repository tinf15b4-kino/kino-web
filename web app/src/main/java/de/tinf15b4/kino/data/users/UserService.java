package de.tinf15b4.kino.data.users;

import de.tinf15b4.kino.data.ServiceModel;

public interface UserService extends ServiceModel<User> {

    User findByName(String username);

    User findByEmail(String email);

}
