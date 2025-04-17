package ApplicationManagmentApp.ApplicationManagmentApp.model;

import java.util.List;

public interface BestApplicantStrategy {
    public Student findBestApplicant(String strategy, List<Application> applications, double fewestCourses, double bestAvgGrade);

}
