package ApplicationManagmentApp.ApplicationManagmentApp.model;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.StudentDAORepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BestAvgGradeStrategy extends TemplateStrategyAlgorithm{

    @Autowired
    private StudentDAORepository studentDAORepository;

    protected int compareApplications(Application app1, Application app2) {
        double avgGrade1 = app1.getStudent().getCurrentAverageGrade();
        double avgGrade2 = app2.getStudent().getCurrentAverageGrade();
        return Double.compare(avgGrade2, avgGrade1);
    }

    protected int getRandomApplication(int size){
        return 0;
    }

    protected int getCustomApplication(List<Application> applications, double fewestCourses, double bestAvgGrade){
        return 0;
    }
}
