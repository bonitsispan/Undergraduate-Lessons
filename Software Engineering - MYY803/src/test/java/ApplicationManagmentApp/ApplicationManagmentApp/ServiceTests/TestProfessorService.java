package ApplicationManagmentApp.ApplicationManagmentApp.ServiceTests;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.ProfessorDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.StudentDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.SubjectDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorService;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorServiceImpl;
import ApplicationManagmentApp.ApplicationManagmentApp.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.Mockito;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

//@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class TestProfessorService {
    @Autowired
    ProfessorService professorService;

    @Autowired
    ProfessorDAORepository professorDAORepository;

    @Autowired
    SubjectDAORepository subjectDAORepository;

    @MockBean
    SubjectDAORepository subjectDAORepositoryMockBean;

    @TestConfiguration
    static class ProfessorServiceImplTestConfiguration{

        @Bean
        public ProfessorService professorService(){
            return new ProfessorServiceImpl();
        }
    }

    @Test
    void testProfessorServiceIsNotNull(){
        Assertions.assertNotNull(this.professorService);
    }

    @Test
    void testUpdatePersonalInformation(){

        Professor professor = new Professor("Harris");
        professor.setFullName("Charalambos Theodoridis");
        professor.setSpeciality("Data");

        this.professorDAORepository.save(professor);

        Subject subject1 = new Subject();
        subject1.setProfessorUsername("Harris");
        subject1.setProfessorFullName("Charalambos Theodoridis");
        Subject subject2 = new Subject();
        subject2.setProfessorUsername("Harris");
        subject2.setProfessorFullName("Charalambos Theodoridis");

        this.subjectDAORepository.save(subject1);
        this.subjectDAORepository.save(subject2);

        this.professorService.setMyProfessor(professor);

        professor.setFullName("Harris Theodoridis");
        professor.setSpeciality("Robotics");

        this.professorService.updatePersonalInformation(professor);

        Assertions.assertEquals("Harris Theodoridis",this.professorService.getMyProfessor().getFullName());
        Assertions.assertEquals("Robotics",this.professorService.getMyProfessor().getSpeciality());

        List<Subject> subjects = this.subjectDAORepository.findByProfessorUsername("Harris");
        for(Subject subject: subjects){
            Assertions.assertEquals("Harris Theodoridis", subject.getProfessorFullName());
        }
    }

    @Test
    void testAssignSubject(){
        Mockito.when(this.subjectDAORepositoryMockBean.findById(1)).thenReturn(new Subject(1,"Zarras"));
        Subject mySubject=this.subjectDAORepositoryMockBean.findById(1);

        mySubject.setTitle("Robotic arm");
        mySubject.setApplicationList(new ArrayList<Application>());

        this.professorService.setMyProfessor(new Professor("Zarras"));
        this.professorService.getMyProfessor().setThesisList(new ArrayList<Thesis>());

        Student bestApplicant=new Student();
        bestApplicant.setYearOfStudies(4);
        bestApplicant.setFullName("Georgios Sidiropoulos");

        this.professorService.assignSubject(bestApplicant,mySubject,"BestAvgGrade",0,0);

        Assertions.assertEquals(0,this.professorService.getMyProfessor().getThesisList().size());

        Application application=new Application();

        mySubject.addApplication(application);

        this.professorService.assignSubject(bestApplicant,mySubject,"BestAvgGrade",0,0);

        Assertions.assertEquals(1,this.professorService.getMyProfessor().getThesisList().size());

        Assertions.assertEquals(1,this.professorService.getMyProfessor().getThesisList().get(0).getId());

        Assertions.assertEquals("Robotic arm",this.professorService.getMyProfessor().getThesisList().get(0).getTopic());

        Assertions.assertEquals("Zarras",this.professorService.getMyProfessor().getThesisList().get(0).getProfessorUsername());

        Assertions.assertEquals("Georgios Sidiropoulos",this.professorService.getMyProfessor().getThesisList().get(0).getStudentFullName());
    }
}
