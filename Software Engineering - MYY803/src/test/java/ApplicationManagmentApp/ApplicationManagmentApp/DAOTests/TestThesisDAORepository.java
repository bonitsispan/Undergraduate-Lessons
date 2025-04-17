package ApplicationManagmentApp.ApplicationManagmentApp.DAOTests;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.SubjectDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.ThesisDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Thesis;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestThesisDAORepository {

    @Autowired
    ThesisDAORepository thesisDAORepository;

    @Test
    @Order(1)
    void testThesisDAORepositoryJpaIsNotNull(){
        Assertions.assertNotNull(this.thesisDAORepository);
    }

    @Test
    @Order(2)
    void testFindByIdReturnsSubject(){
        Thesis storedThesis=this.thesisDAORepository.findById(1);
        Assertions.assertNotNull(storedThesis);
        Assertions.assertEquals(1,storedThesis.getId());
    }

    @Test
    @Order(3)
    void testDeleteByIdReturnsNullSubject(){
        Thesis storedThesis=this.thesisDAORepository.findById(1);

        Assertions.assertNotNull(storedThesis);
        Assertions.assertEquals(1,storedThesis.getId());

        this.thesisDAORepository.deleteById(1);
        Assertions.assertNull(this.thesisDAORepository.findById(1));
    }

    @Test
    @Order(4)
    void testFindByProfessorUsernameReturnsListSubject(){
        List<Thesis> storedThesis=this.thesisDAORepository.findByProfessorUsername("Zarras");

        Assertions.assertNotNull(storedThesis);
        Assertions.assertEquals(2,storedThesis.size());
    }

    @Test
    @Order(5)
    void testFindByStudentUsernameReturnsListSubject(){
        List<Thesis> storedThesis=this.thesisDAORepository.findByStudentUsername("Harris");

        Assertions.assertNotNull(storedThesis);
        Assertions.assertEquals(2,storedThesis.size());
    }

}
