package ApplicationManagmentApp.ApplicationManagmentApp.ServiceTests;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.StudentDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.SubjectDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Application;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.service.StudentServiceImpl;
import ApplicationManagmentApp.ApplicationManagmentApp.service.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestStudentService {
    @Autowired
    StudentService studentService=new StudentServiceImpl();

    @MockBean
    SubjectDAORepository subjectDAORepository;

    @MockBean
    StudentDAORepository studentDAORepository;

    @TestConfiguration
    static class StudentServiceImplTestConfiguration{

        @Bean
        public StudentService studentService(){
            return new StudentServiceImpl();
        }
    }

    @Test
    void testStudentServiceIsNotNull(){
        Assertions.assertNotNull(studentService);
    }

    @Test
    void testApplyToSubject(){
        Mockito.when(this.studentDAORepository.findByUsername("Pantelis")).thenReturn(new Student("Pantelis"));

        Student myStudent=this.studentDAORepository.findByUsername("Pantelis");

        this.studentService.setMyStudent(myStudent);

        Mockito.when(this.subjectDAORepository.findById(1)).thenReturn(new Subject(1,"Zarras"));

        Subject subject=this.subjectDAORepository.findById(1);

        subject.setApplicationList(new ArrayList<Application>());

        this.studentService.applyToSubject(subject.getId());

        Assertions.assertNotNull(subject.getApplicationList());
        Assertions.assertEquals(1,subject.getApplicationList().size());
        Assertions.assertEquals("Pantelis",subject.getApplicationList().get(0).getStudent().getUsername());

        this.studentService.applyToSubject(subject.getId());

        Assertions.assertEquals(1,subject.getApplicationList().size());
    }
}
