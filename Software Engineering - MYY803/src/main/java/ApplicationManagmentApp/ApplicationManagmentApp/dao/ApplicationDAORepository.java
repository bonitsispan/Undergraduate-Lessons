package ApplicationManagmentApp.ApplicationManagmentApp.dao;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationDAORepository extends JpaRepository<Application,Integer> {

    public void deleteById(int id);

    public Application findById(int id);  // Method created for test
}
