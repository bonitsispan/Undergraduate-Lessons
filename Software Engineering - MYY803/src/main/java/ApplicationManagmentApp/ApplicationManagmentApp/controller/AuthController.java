package ApplicationManagmentApp.ApplicationManagmentApp.controller;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Professor;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import ApplicationManagmentApp.ApplicationManagmentApp.model.User;
import ApplicationManagmentApp.ApplicationManagmentApp.service.ProfessorService;
import ApplicationManagmentApp.ApplicationManagmentApp.service.StudentService;
import ApplicationManagmentApp.ApplicationManagmentApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    ProfessorService professorService;
    @Autowired
    StudentService studentService;
    private User myUser;

    @RequestMapping("/login")
    public String login(){

        return "auth/signin";
    }

    @RequestMapping("/register")
    public String register(Model model){

        model.addAttribute("user", new User());
        return "auth/signup";
    }

    @RequestMapping("/save")
    public String registerUser(@ModelAttribute("user") User user, Model model){

        if(userService.isUserPresent(user)){
            model.addAttribute("successMessage", "User already registered!");
            return "auth/signin";
        }

        userService.saveUser(user);
        model.addAttribute("successMessage", "User registered successfully!");

        this.myUser = user;

        if(user.getRole().getValue().equals("Professor")){
            Professor newProfessor = new Professor();
            model.addAttribute("newProfessor", newProfessor);
            return "/auth/registerForm-Professor";
        }

        if(user.getRole().getValue().equals("Student")){
            Student newStudent = new Student();
            model.addAttribute("newStudent", newStudent);
            return "/auth/registerForm-Student";
        }

        return "auth/signin";
    }

    @RequestMapping("/saveProfessor")
    public String saveProfessorProfile(@ModelAttribute("newProfessor") Professor newProfessor){

        newProfessor.setUsername(this.myUser.getUsername());

        professorService.saveProfile(newProfessor);

        return "auth/signin";
    }

    @RequestMapping("/saveStudent")
    public String saveStudentProfile(@ModelAttribute("newStudent") Student newStudent){

        newStudent.setUsername(this.myUser.getUsername());

        studentService.saveProfile(newStudent);

        return "auth/signin";
    }
}
