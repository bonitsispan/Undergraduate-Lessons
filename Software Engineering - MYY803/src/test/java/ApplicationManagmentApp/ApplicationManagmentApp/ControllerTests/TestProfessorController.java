package ApplicationManagmentApp.ApplicationManagmentApp.ControllerTests;

import ApplicationManagmentApp.ApplicationManagmentApp.controller.ProfessorController;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Application;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Thesis;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
public class TestProfessorController {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProfessorController professorController;

    @MockBean
    ProfessorService professorService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void testEmployeeControllerIsNotNull() {
        Assertions.assertNotNull(professorController);
    }

    @Test
    void testMockMvcIsNotNull() {
        Assertions.assertNotNull(mockMvc);
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testRetriveProfileReturnsPage() throws Exception {

        mockMvc.perform(get("/professor/retriveProfile")).
                andExpect(status().isOk()). // adds result matchers that check some property
                andExpect(view().name("professor/professor-main-menu")); // adds view matchers that check some property
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetProfessorMainMenuReturnsPage() throws Exception {

        mockMvc.perform(get("/professor/getProfessorMainMenu")).
                andExpect(status().isOk()). // adds result matchers that check some property
                andExpect(view().name("professor/professor-main-menu")); // adds view matchers that check some property
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetFormOfProfessorInformationReturnsPage() throws Exception{

        Professor updatedProfessor = new Professor("pantelis", "Pantelis Bonitsis");

        when(professorService.getMyProfessor()).thenReturn(updatedProfessor);

        mockMvc.perform(get("/professor/FormOfProfessorInformation"))
                .andExpect(status().isOk())
                .andExpect(view().name("/professor/professor-new-register-form"))
                .andExpect(model().attributeExists("updatedProfessor"))
                .andExpect(model().attribute("updatedProfessor", updatedProfessor));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testSaveProfessorUpdatedProfileReturnsPage() throws Exception{

        Professor updatedProfessor = new Professor("pantelis", "Pantelis Bonitsis");

        mockMvc.perform(post("/professor/saveUpdatedProfessor")
                        .flashAttr("updatedProfessor", updatedProfessor))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/professor/getProfessorMainMenu"));

    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetListProfessorSubjectsReturnsPage() throws Exception{

        List<Subject> mySubjects = new ArrayList<>();

        when(professorService.listProfessorSubjects()).thenReturn(mySubjects);

        mockMvc.perform(get("/professor/listProfessorSubjects"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor-list-subjects"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("subjects", mySubjects));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testShowFormForAddSubjectReturnsPage() throws Exception{

        mockMvc.perform(get("/professor/showFormForAddSubject"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor-new-subject-form"))
                .andExpect(model().attributeExists("newSubject"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testSaveNewSubjectReturnsPage() throws Exception {

        Subject newSubject = new Subject(1);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("id", Integer.toString(newSubject.getId()));
        multiValueMap.add("professorUsername", newSubject.getProfessorUsername());
        multiValueMap.add("professorFullName", newSubject.getProfessorFullName());
        multiValueMap.add("objectives", newSubject.getObjectives());
        multiValueMap.add("title", newSubject.getTitle());

        when(professorService.getMyProfessor()).thenReturn(new Professor("Pantelis", "Pantelis Bonitsis"));

        mockMvc.perform(
                        post("/professor/saveNewSubject")
                                .params(multiValueMap))
                                .andExpect(status().isFound())
                                .andExpect(view().name("redirect:/professor/listProfessorSubjects"));

    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testShowFormForUpdateSubjectReturnsPage() throws Exception{

        Subject updateSubject = new Subject(1);

        when(professorService.findSubjectById(1)).thenReturn(updateSubject);

        mockMvc.perform(get("/professor/showFormForUpdateSubject").param("subjectId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor-update-subject-form"))
                .andExpect(model().attributeExists("updatedSubject"))
                .andExpect(model().attribute("updatedSubject", updateSubject));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testSaveUpdatedSubjectReturnsPage() throws Exception{

        Subject updatedSubject = new Subject(1);

        mockMvc.perform(post("/professor/saveUpdatedSubject")
                        .flashAttr("updatedSubject", updatedSubject))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/professor/listProfessorSubjects"));

    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testDeleteSubjectReturnsPage() throws Exception{

        int subjectId = 1;

        mockMvc.perform(post("/professor/deleteSubject").param("subjectId", String.valueOf(subjectId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/listProfessorSubjects"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetListApplicationsReturnsPage() throws Exception{

        List<Application> applications = new ArrayList<>();

        when(professorService.listApplications(1)).thenReturn(applications);

        mockMvc.perform(get("/professor/getListApplications").param("subjectId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor-list-subject-applications"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attribute("applications", applications));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testGetListProfessorThesisReturnsPage() throws Exception{

        List<Thesis> myThesis = new ArrayList<>();

        when(professorService.listProfessorThesis()).thenReturn(myThesis);

        mockMvc.perform(get("/professor/listProfessorThesis"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor-list-theses"))
                .andExpect(model().attributeExists("theses"))
                .andExpect(model().attribute("theses", myThesis));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testAddThesisGradesReturnsPage() throws Exception{

        Thesis updatedThesis = new Thesis(1);

        when(professorService.findThesisById(1)).thenReturn(updatedThesis);

        mockMvc.perform(get("/professor/addThesisGrades").param("thesisId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor-add-thesis-grades-form"))
                .andExpect(model().attributeExists("updatedThesis"))
                .andExpect(model().attribute("updatedThesis", updatedThesis));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testSaveUpdatedThesisReturnsPage() throws Exception{

        Thesis updatedThesis = new Thesis(1);

        mockMvc.perform(post("/professor/saveUpdatedThesis")
                        .flashAttr("updatedThesis", updatedThesis))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/professor/listProfessorThesis"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testDeleteThesisReturnsPage() throws Exception{

        int thesisId = 1;

        mockMvc.perform(post("/professor/deleteThesis").param("thesisId", String.valueOf(thesisId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/listProfessorThesis"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testShowFormForAssignSubjectReturnsPage() throws Exception{

        int subjectId = 1;

        mockMvc.perform(get("/professor/showFormForAssignSubject").param("subjectId", String.valueOf(subjectId)))
                .andExpect(status().isOk())
                .andExpect(view().name("/professor/professor-choose-strategy-form"));

    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testStrategyRandomChoiceReturnsPage() throws Exception {

        mockMvc.perform(get("/professor/strategyRandomChoice"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/listProfessorThesis"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testStrategyBestAvgReturnsPage() throws Exception {

        mockMvc.perform(get("/professor/strategyBestAvg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/listProfessorThesis"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testStrategyFewestCoursesReturnsPage() throws Exception {

        mockMvc.perform(get("/professor/strategyFewestCourses"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/listProfessorThesis"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testShowFormForCustomChoiceReturnsPage() throws Exception {

        mockMvc.perform(get("/professor/showFormForCustomChoice"))
                .andExpect(status().isOk())
                .andExpect(view().name("/professor/professor-custom-choice-form"));
    }

    @WithMockUser(value = "Pantelis")
    @Test
    void testStrategyCustomReturnsPage() throws Exception{

        double fewestCoursesLimit = 5;
        double bestAvgGradeLimit = 8;

        mockMvc.perform(get("/professor/strategyCustom")
                        .param("fewestCoursesLimit", String.valueOf(fewestCoursesLimit))
                        .param("bestAvgGradeLimit", String.valueOf(bestAvgGradeLimit)))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/professor/listProfessorThesis"));
    }

}
