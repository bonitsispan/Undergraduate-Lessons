package ApplicationManagmentApp.ApplicationManagmentApp.ModelTests;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Application;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorService;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class TestSubject {

    //@Autowired
    Subject subject = new Subject(1,"Pantelis");


    @TestConfiguration
    static class SubjectTestConfiguration{

        @Bean
        public Subject subject(){
            return new Subject();
        }
    }

    @Test
    void testSubjectIsNotNull(){
        Assertions.assertNotNull(this.subject);
    }

    @Test
    void testFindBestApplicant(){

        Student harris = new Student("harris");
        harris.setNumberOfRemainingCourses(4);
        harris.setCurrentAverageGrade(9);
        Application application1 = new Application(harris, 1);

        Student pantelis = new Student("pantelis");
        pantelis.setNumberOfRemainingCourses(8);
        pantelis.setCurrentAverageGrade(9.5);
        Application application2 = new Application(pantelis, 1);

        Student giorgos = new Student("giorgos");
        giorgos.setNumberOfRemainingCourses(3);
        giorgos.setCurrentAverageGrade(6);
        Application application3 = new Application(giorgos, 1);

        List<Application> applicationList = new ArrayList<>();
        applicationList.add(application1);
        applicationList.add(application2);
        applicationList.add(application3);

        this.subject.setApplicationList(applicationList);


        Student bestApplicant1 = this.subject.findBestApplicant("CustomChoice", 5, 8.5);

        Assertions.assertEquals("harris", bestApplicant1.getUsername());


        Student bestApplicant2 = this.subject.findBestApplicant("FewestCourses", 0, 0);

        Assertions.assertEquals("giorgos", bestApplicant2.getUsername());


        Student bestApplicant3 = this.subject.findBestApplicant("BestAvgGrade", 0, 0);

        Assertions.assertEquals("pantelis", bestApplicant3.getUsername());

    }
}
