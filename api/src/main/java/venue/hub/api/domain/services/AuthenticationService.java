package venue.hub.api.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import venue.hub.api.domain.entities.User;
import venue.hub.api.domain.repositories.UserRepository;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByLogin(email);
    }

    public User getAuthenticatedUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
