package ApplicationManagmentApp.ApplicationManagmentApp.service;

import ApplicationManagmentApp.ApplicationManagmentApp.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public void  saveUser(User user);

    public  boolean isUserPresent(User user);

    public UserDetails loadUserByUsername(String username);
}
