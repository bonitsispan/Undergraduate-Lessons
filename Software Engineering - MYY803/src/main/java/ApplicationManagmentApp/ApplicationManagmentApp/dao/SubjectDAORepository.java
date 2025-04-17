package ApplicationManagmentApp.ApplicationManagmentApp.dao;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectDAORepository extends JpaRepository<Subject,Integer> {

    public void deleteById(int id);
    public List<Subject> findByProfessorUsername(String professor_username);

    public Subject findById(int id);
}
