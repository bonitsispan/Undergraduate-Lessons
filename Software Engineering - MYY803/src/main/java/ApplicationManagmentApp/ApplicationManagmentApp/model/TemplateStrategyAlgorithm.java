package ApplicationManagmentApp.ApplicationManagmentApp.model;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.StudentDAORepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class TemplateStrategyAlgorithm implements BestApplicantStrategy {

    @Autowired
    private StudentDAORepository studentDAORepository;


    public Student findBestApplicant(String strategy, List<Application> applications, double fewestCourses, double bestAvgGrade){

        if(strategy.equals("RandomChoice")){
            return findRandomApplicant(applications);
        }
        if(strategy.equals("CustomChoice")){
            return findCustomApplicant(applications, fewestCourses, bestAvgGrade);
        }
        return findCompareApplicant(applications);
    }

    private Student findCompareApplicant(List<Application> applications) {
        Application bestApplication = applications.get(0);
        for (Application application : applications) {
            if (compareApplications(application, bestApplication) < 0) {
                bestApplication = application;
            }
        }
        return bestApplication.getStudent();
    }

    private Student findRandomApplicant(List<Application> applications){
        return applications.get(getRandomApplication(applications.size())).getStudent();
    }

    private Student findCustomApplicant(List<Application> applications, double fewestCourses, double bestAvgGrade){
        if(getCustomApplication(applications, fewestCourses, bestAvgGrade) == -1){
            Student emptyStudent = new Student();
            emptyStudent.setYearOfStudies(-1);
            return emptyStudent;
        }
        return applications.get(getCustomApplication(applications, fewestCourses, bestAvgGrade)).getStudent();
    }

    protected abstract int compareApplications(Application app1, Application app2);

    protected abstract int getRandomApplication(int size);

    protected abstract int getCustomApplication(List<Application> applications, double fewestCourses, double bestAvgGrade);

}
