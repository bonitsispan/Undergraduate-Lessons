package ApplicationManagmentApp.ApplicationManagmentApp.controller;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Application;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Thesis;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorService;
import ApplicationManagmentApp.ApplicationManagmentApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    ProfessorService professorService;

    @RequestMapping("/retriveProfile")
    public String retriveProfile(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        professorService.retrieveProfile(currentPrincipalName);

        return "professor/professor-main-menu";
    }

    @RequestMapping("/getProfessorMainMenu")
    public String getProfessorMainMenu(){

        return "professor/professor-main-menu";
    }

    @RequestMapping("/FormOfProfessorInformation")
    public String getFormOfProfessorInformation(Model model){

        model.addAttribute("updatedProfessor", professorService.getMyProfessor());

        return "/professor/professor-new-register-form";
    }

    @RequestMapping("/saveUpdatedProfessor")
    public String saveProfessorUpdatedProfile(@ModelAttribute("updatedProfessor") Professor updatedProfessor){

        professorService.updatePersonalInformation(updatedProfessor);

        return "redirect:/professor/getProfessorMainMenu";
    }

    @RequestMapping("/listProfessorSubjects")
    public String getListProfessorSubjects(Model model){

        List<Subject> mySubjects = professorService.listProfessorSubjects();
        model.addAttribute("subjects", mySubjects);

        return "professor/professor-list-subjects";
    }

    @RequestMapping("/showFormForAddSubject")
    public String showFormForAddSubject(Model model) {

        Subject newSubject = new Subject();
        model.addAttribute("newSubject", newSubject);

        return "professor/professor-new-subject-form";
    }

    @RequestMapping("/saveNewSubject")
    public String saveNewSubject(@ModelAttribute("newSubject") Subject newSubject) {

        newSubject.setProfessorUsername(professorService.getMyProfessor().getUsername());
        newSubject.setProfessorFullName(professorService.getMyProfessor().getFullName());

        professorService.addSubject(newSubject);

        return "redirect:/professor/listProfessorSubjects";
    }

    @RequestMapping("/showFormForUpdateSubject")
    public String showFormForUpdateSubject(@RequestParam("subjectId") int id, Model model){

        Subject subject = professorService.findSubjectById(id);

        model.addAttribute("updatedSubject", subject);

        return "professor/professor-update-subject-form";

    }

    @RequestMapping("/saveUpdatedSubject")
    public String saveUpdatedSubject(@ModelAttribute("updatedSubject") Subject updatedSubject) {

        professorService.addSubject(updatedSubject);

        return "redirect:/professor/listProfessorSubjects";
    }

    @RequestMapping("/deleteSubject")
    public String deleteSubject(@RequestParam("subjectId") int id) {

        professorService.deleteSubject(id);

        return "redirect:/professor/listProfessorSubjects";
    }

    @RequestMapping("/getListApplications")
    public String getListApplications(@RequestParam("subjectId") int id, Model model){

        List<Application> myApplications = professorService.listApplications(id);

        model.addAttribute("applications", myApplications);

        return "professor/professor-list-subject-applications";
    }

    @RequestMapping("/listProfessorThesis")
    public String getListProfessorThesis(Model model){

        List<Thesis> myThesis = professorService.listProfessorThesis();
        model.addAttribute("theses", myThesis);

        return "professor/professor-list-theses";
    }

    @RequestMapping("/addThesisGrades")
    public String addThesisGrades(@RequestParam("thesisId") int id, Model model){

        Thesis thesis = professorService.findThesisById(id);
        model.addAttribute("updatedThesis", thesis);

        return "professor/professor-add-thesis-grades-form";
    }

    @RequestMapping("/saveUpdatedThesis")
    public String saveUpdatedThesis(@ModelAttribute("updatedThesis") Thesis updatedThesis) {

        professorService.updateThesisGrades(updatedThesis);
        professorService.calculateFinalThesisGrade(updatedThesis);

        return "redirect:/professor/listProfessorThesis";
    }

    @RequestMapping("/deleteThesis")
    public String deleteThesis(@RequestParam("thesisId") int id){

        professorService.deleteThesis(id);

        return "redirect:/professor/listProfessorThesis";
    }

    @RequestMapping("/showFormForAssignSubject")
    public String showFormForAssignSubject(@RequestParam("subjectId") int id) {

        professorService.setMyAssignSubjectId(id);

        return "/professor/professor-choose-strategy-form";
    }

    @RequestMapping("/strategyRandomChoice")
    public String strategyRandomChoice() {

        professorService.assignSubject("RandomChoice", 0, 0);

        return "redirect:/professor/listProfessorThesis";
    }

    @RequestMapping("/strategyBestAvg")
    public String strategyBestAvg() {

        professorService.assignSubject("BestAvgGrade", 0, 0);

        return "redirect:/professor/listProfessorThesis";
    }

    @RequestMapping("/strategyFewestCourses")
    public String strategyFewestCourses() {

        professorService.assignSubject("FewestCourses", 0, 0);

        return "redirect:/professor/listProfessorThesis";
    }

    @RequestMapping("/showFormForCustomChoice")
    public String showFormForCustomChoice() {

        return "/professor/professor-custom-choice-form";
    }

    @RequestMapping("/strategyCustom")
    public String strategyCustom(@RequestParam("fewestCoursesLimit") double fewestCoursesLimit, @RequestParam("bestAvgGradeLimit") double bestAvgGradeLimit) {

        professorService.assignSubject("CustomChoice", fewestCoursesLimit, bestAvgGradeLimit);

        return "redirect:/professor/listProfessorThesis";
    }

}
