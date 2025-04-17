package ApplicationManagmentApp.ApplicationManagmentApp.dao;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Thesis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThesisDAORepository extends JpaRepository<Thesis,Integer> {

    public Thesis findById(int id);

    public void deleteById(int id);

    public List<Thesis> findByProfessorUsername(String professorUsername);

    public List<Thesis> findByStudentUsername(String studentUsername);
}
