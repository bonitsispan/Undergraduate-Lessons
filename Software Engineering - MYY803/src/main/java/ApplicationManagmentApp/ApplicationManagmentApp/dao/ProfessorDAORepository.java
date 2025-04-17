package ApplicationManagmentApp.ApplicationManagmentApp.dao;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorDAORepository extends JpaRepository<Professor,String> {

    public Professor findByUsername(String username);
}
