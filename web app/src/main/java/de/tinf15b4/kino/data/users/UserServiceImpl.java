package de.tinf15b4.kino.data.users;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import de.tinf15b4.kino.data.User;
import de.tinf15b4.kino.data.UserRepository;

@Transactional
@Component
@ApplicationScope
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(long id) {
        return userRepository.findOne(id);
    }

    @Override
    public User findByName(String username) {
        Optional<User> userOptional = userRepository.findAll().stream().filter(u -> u.getName().equals(username))
                .findFirst();
        if (userOptional.isPresent())
            return userOptional.get();
        return null;
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> userOptional = userRepository.findAll().stream().filter(u -> u.getEmail().equals(email))
                .findFirst();
        if (userOptional.isPresent())
            return userOptional.get();
        return null;
    }
}
