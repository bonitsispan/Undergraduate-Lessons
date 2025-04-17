package ApplicationManagmentApp.ApplicationManagmentApp.controller;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorService;
import ApplicationManagmentApp.ApplicationManagmentApp.service.StudentService;
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

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    @RequestMapping("/retriveProfile")
    public String retriveProfile(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        studentService.retrieveProfile(currentPrincipalName);

        return "student/student-main-menu";
    }

    @RequestMapping("/getStudentMainMenu")
    public String getProfessorMainMenu(){

        return "student/student-main-menu";
    }

    @RequestMapping("/FormOfStudentInformation")
    public String getFormOfStudentInformation(Model model){

        model.addAttribute("updatedStudent", studentService.getMyStudent());

        return "/student/student-new-register-form";
    }

    @RequestMapping("/saveUpdatedStudent")
    public String saveStudentUpdatedProfile(@ModelAttribute("updatedStudent") Student updatedStudent){

        studentService.updatePersonalInformation(updatedStudent);

        return "redirect:/student/getStudentMainMenu";
    }

    @RequestMapping("/getAvailableSubjects")
    public String getAvailableSubjects(Model model){

        List<Subject> availableSubjects = studentService.getAvailableSubjects();
        model.addAttribute("availableSubjects", availableSubjects);

        return "student/student-list-available-subjects";
    }

    @RequestMapping("/getSubjectInformation")
    public String getSubjectInformation(@RequestParam("subjectId") int id, Model model){

        Subject subject = studentService.getSubjectInformation(id);
        model.addAttribute("subject", subject);

        return "/student/student-subject-information";
    }

    @RequestMapping("/applyToSubject")
    public String applyToSubject(@RequestParam("subjectId") int id){

        studentService.applyToSubject(id);

        return "redirect:/student/getStudentMainMenu";
    }

}
