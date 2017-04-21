package de.tinf15b4.kino.data.users;

import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.ServiceImplModel;

@Service
public class UserServiceImpl extends ServiceImplModel<User, UserRepository> implements UserService {

    @Override
    public User findByName(String username) {
        return repository.findByName(username);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
