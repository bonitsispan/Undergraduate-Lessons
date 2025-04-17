package ApplicationManagmentApp.ApplicationManagmentApp.service;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.UserDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserDAORepository userDAORepository;

    @Override
    public void saveUser(User user){

        String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        userDAORepository.save(user);
    }

    @Override
    public  boolean isUserPresent(User user){

        Optional<User> storeUser = userDAORepository.findByUsername(user.getUsername());
        return storeUser.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDAORepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("USER_NOT_FOUND", username)
                ));
    }
}
