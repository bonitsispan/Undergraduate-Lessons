package ApplicationManagmentApp.ApplicationManagmentApp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.User;

import java.util.Optional;

public interface UserDAORepository extends JpaRepository<User,Integer>  {
    Optional<User> findByUsername(String username);
}
