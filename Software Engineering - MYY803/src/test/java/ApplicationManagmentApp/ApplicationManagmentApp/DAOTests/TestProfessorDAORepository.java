package ApplicationManagmentApp.ApplicationManagmentApp.DAOTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.ProfessorDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class TestProfessorDAORepository {
    @Autowired
    ProfessorDAORepository professorDAORepository;

    @Test
    void testProfessorDAORepositoryJpaIsNotNull(){
        Assertions.assertNotNull(this.professorDAORepository);
    }

    @Test
    void testFindByUsernameReturnsProfessor(){
        Professor storedProfessor=this.professorDAORepository.findByUsername("Zarras");
        Assertions.assertNotNull(storedProfessor);
        Assertions.assertEquals("Zarras",storedProfessor.getUsername());
    }
}
