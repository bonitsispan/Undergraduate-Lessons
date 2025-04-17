package ApplicationManagmentApp.ApplicationManagmentApp.model;

import java.util.List;

public class CustomChoiceStrategy extends TemplateStrategyAlgorithm{

    protected int getCustomApplication(List<Application> applications, double fewestCourses, double bestAvgGrade){
        Application firstApplication = applications.get(0);
        for (int index = 0; index < applications.size(); index++) {
            if((applications.get(index).getStudent().getNumberOfRemainingCourses() < fewestCourses) && (applications.get(index).getStudent().getCurrentAverageGrade() > bestAvgGrade)){
                return index;
            }
        }
        return -1;
    }
    protected int compareApplications(Application app1, Application app2) {
        return 0;
    }

    protected int getRandomApplication(int size){
        return 0;
    }
}
