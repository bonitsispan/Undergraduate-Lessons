package ApplicationManagmentApp.ApplicationManagmentApp.model;

public class BestApplicantStrategyFactory {

    public BestApplicantStrategy getStrategy(String strategyType) {
        if (strategyType.equals("BestAvgGrade")) {
            return new BestAvgGradeStrategy();
        } else if (strategyType.equals("FewestCourses")) {
            return new FewestCoursesStrategy();
        }else if (strategyType.equals("RandomChoice")) {
            return new RandomChoiceStrategy();
        }else if (strategyType.equals("CustomChoice")) {
            return new CustomChoiceStrategy();
        }else {
            throw new IllegalArgumentException("Invalid strategy type: " + strategyType);
        }
    }

}
