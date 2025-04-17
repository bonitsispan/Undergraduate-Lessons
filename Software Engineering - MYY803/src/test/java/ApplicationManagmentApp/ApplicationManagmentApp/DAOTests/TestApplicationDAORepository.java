package ApplicationManagmentApp.ApplicationManagmentApp.DAOTests;


import ApplicationManagmentApp.ApplicationManagmentApp.dao.ApplicationDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.SubjectDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Application;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class TestApplicationDAORepository {
    @Autowired
    ApplicationDAORepository applicationDAORepository;

    @Test
    void testApplicationDAORepositoryJpaIsNotNull(){
        Assertions.assertNotNull(this.applicationDAORepository);
    }

    @Test
    void testDeleteByIdReturnsNullApplication(){
        Application storedApplication=this.applicationDAORepository.findById(1);

        Assertions.assertNotNull(storedApplication);
        Assertions.assertEquals(1,storedApplication.getId());

        this.applicationDAORepository.deleteById(1);
        Assertions.assertNull(this.applicationDAORepository.findById(1));
    }
}
