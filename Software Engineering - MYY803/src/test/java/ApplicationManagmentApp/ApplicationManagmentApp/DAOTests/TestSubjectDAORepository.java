package ApplicationManagmentApp.ApplicationManagmentApp.DAOTests;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.SubjectDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSubjectDAORepository {

    @Autowired
    SubjectDAORepository subjectDAORepository;

    @Test
    @Order(1)
    void testSubjectDAORepositoryJpaIsNotNull(){
        Assertions.assertNotNull(this.subjectDAORepository);
    }

   @Test
   @Order(2)
    void testFindByIdReturnsSubject(){
        Subject storedSubject=this.subjectDAORepository.findById(1);
        Assertions.assertNotNull(storedSubject);
        Assertions.assertEquals(1,storedSubject.getId());
    }

    @Test
    @Order(3)
    void testDeleteByIdReturnsNullSubject(){
        Subject storedSubject=this.subjectDAORepository.findById(1);

        Assertions.assertNotNull(storedSubject);
        Assertions.assertEquals(1,storedSubject.getId());

        this.subjectDAORepository.deleteById(1);
        Assertions.assertNull(this.subjectDAORepository.findById(1));
    }

    @Test
    @Order(4)
    void testFindByProfessorUsernameReturnsListSubject(){
        List<Subject> storedSubjects=this.subjectDAORepository.findByProfessorUsername("Zarras");

        Assertions.assertNotNull(storedSubjects);
        Assertions.assertEquals(2,storedSubjects.size());
    }
}
