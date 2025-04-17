package ApplicationManagmentApp.ApplicationManagmentApp.model;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.StudentDAORepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FewestCoursesStrategy extends TemplateStrategyAlgorithm{

    @Autowired
    private StudentDAORepository studentDAORepository;

    protected int compareApplications(Application app1, Application app2) {
        int numCourses1 = app1.getStudent().getNumberOfRemainingCourses();
        int numCourses2 = app2.getStudent().getNumberOfRemainingCourses();
        return Integer.compare(numCourses1, numCourses2);
    }

    protected int getRandomApplication(int size){
        return 0;
    }

    protected int getCustomApplication(List<Application> applications, double fewestCourses, double bestAvgGrade){
        return 0;
    }
}
