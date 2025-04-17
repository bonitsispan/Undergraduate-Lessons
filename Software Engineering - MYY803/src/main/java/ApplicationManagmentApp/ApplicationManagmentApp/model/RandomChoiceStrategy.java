package ApplicationManagmentApp.ApplicationManagmentApp.model;

import java.util.List;
import java.util.Random;

public class RandomChoiceStrategy extends TemplateStrategyAlgorithm{
    protected int compareApplications(Application app1, Application app2) {
      return 0;
    }

    protected int getRandomApplication(int size){
        Random random = new Random();
        return random.nextInt(size);
    }

    protected int getCustomApplication(List<Application> applications, double fewestCourses, double bestAvgGrade){
        return 0;
    }

}
