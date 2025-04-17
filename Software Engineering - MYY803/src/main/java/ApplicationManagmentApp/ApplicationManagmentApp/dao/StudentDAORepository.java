package ApplicationManagmentApp.ApplicationManagmentApp.dao;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDAORepository  extends JpaRepository<Student,String> {
    public Student findByUsername(String username);
}
