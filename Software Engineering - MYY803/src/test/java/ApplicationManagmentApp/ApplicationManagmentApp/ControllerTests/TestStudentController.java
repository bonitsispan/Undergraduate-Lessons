package ApplicationManagmentApp.ApplicationManagmentApp.ControllerTests;

import ApplicationManagmentApp.ApplicationManagmentApp.controller.StudentController;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.service.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class TestStudentController {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    StudentController studentController;

    @MockBean
    StudentService studentService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void testEmployeeControllerIsNotNull() {
        Assertions.assertNotNull(studentController);
    }

    @Test
    void testMockMvcIsNotNull() {
        Assertions.assertNotNull(mockMvc);
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testRetriveProfileReturnsPage() throws Exception {

        mockMvc.perform(get("/student/retriveProfile")).
                andExpect(status().isOk()). // adds result matchers that check some property
                andExpect(view().name("student/student-main-menu")); // adds view matchers that check some property
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetStudentMainMenuReturnsPage() throws Exception {

        mockMvc.perform(get("/student/getStudentMainMenu")).
                andExpect(status().isOk()). // adds result matchers that check some property
                andExpect(view().name("student/student-main-menu")); // adds view matchers that check some property
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetFormOfStudentInformationReturnsPage() throws Exception{

        Student updatedStudent = new Student("pantelis", "Pantelis Bonitsis");

        when(studentService.getMyStudent()).thenReturn(updatedStudent);

        mockMvc.perform(get("/student/FormOfStudentInformation"))
                .andExpect(status().isOk())
                .andExpect(view().name("/student/student-new-register-form"))
                .andExpect(model().attributeExists("updatedStudent"))
                .andExpect(model().attribute("updatedStudent", updatedStudent));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testSaveStudentUpdatedProfileReturnsPage() throws Exception{

        Student updatedStudent = new Student("pantelis", "Pantelis Bonitsis");

        mockMvc.perform(post("/student/saveUpdatedStudent")
                        .flashAttr("updatedStudent", updatedStudent))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/student/getStudentMainMenu"));

    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetAvailableSubjectsReturnsPage() throws Exception{

        List<Subject> availableSubjects = new ArrayList<>();

        when(studentService.getAvailableSubjects()).thenReturn(availableSubjects);

        mockMvc.perform(get("/student/getAvailableSubjects"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student-list-available-subjects"))
                .andExpect(model().attributeExists("availableSubjects"))
                .andExpect(model().attribute("availableSubjects", availableSubjects));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetSubjectInformationReturnsPage() throws Exception{

        Subject subject = new Subject(1);

        when(studentService.getSubjectInformation(1)).thenReturn(subject);

        mockMvc.perform(get("/student/getSubjectInformation").param("subjectId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/student/student-subject-information"))
                .andExpect(model().attributeExists("subject"))
                .andExpect(model().attribute("subject", subject));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testApplyToSubjectReturnsPage() throws Exception{

        int subjectId = 1;

        mockMvc.perform(post("/student/applyToSubject").param("subjectId", String.valueOf(subjectId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/getStudentMainMenu"));
    }
}
