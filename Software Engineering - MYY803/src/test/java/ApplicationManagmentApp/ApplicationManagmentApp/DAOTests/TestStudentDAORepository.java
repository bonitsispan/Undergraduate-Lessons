package ApplicationManagmentApp.ApplicationManagmentApp.DAOTests;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.StudentDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class TestStudentDAORepository {
    @Autowired
    StudentDAORepository studentDAORepository;
    @Test
    void testStudentDAORepositoryJpaIsNotNull(){
        Assertions.assertNotNull(this.studentDAORepository);
    }

    @Test
    void testFindByUsernameReturnsStudent(){
        Student storedStudent=this.studentDAORepository.findByUsername("Harris");

        Assertions.assertNotNull(storedStudent);
        Assertions.assertEquals("Harris",storedStudent.getUsername());
    }
}

