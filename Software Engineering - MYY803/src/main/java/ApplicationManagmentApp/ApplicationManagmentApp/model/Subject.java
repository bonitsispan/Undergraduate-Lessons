package ApplicationManagmentApp.ApplicationManagmentApp.model;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="subject")
public class Subject {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="professorusername")
    private String professorUsername;

    @Column(name="professorfullname")
    private String professorFullName;

    @OneToMany(fetch=FetchType.EAGER ,cascade=CascadeType.ALL)
    @JoinColumn(name="subjectid")
    private List<Application> applicationList;

    @Column(name="objectives")
    private String objectives;

    @Column(name="title")
    private String title;


    public Subject() {

    }

    public Subject(int id) {
        this.id = id;
    }

    public Subject(int id,String professorUsername) {
        this.id = id;
        this.professorUsername=professorUsername;
    }

    public Student findBestApplicant(String strategy, double fewestCourses, double bestAvgGrade){

        BestApplicantStrategyFactory bestApplicantStrategyFactory = new BestApplicantStrategyFactory();

        return bestApplicantStrategyFactory.getStrategy(strategy).findBestApplicant(strategy, this.applicationList, fewestCourses, bestAvgGrade);
    }

    public void addApplication(Application application){
        this.applicationList.add(application);
    }

    public List<Application> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<Application> applicationList) {
        this.applicationList = applicationList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfessorFullName() {
        return professorFullName;
    }

    public void setProfessorFullName(String professorFullName) {
        this.professorFullName = professorFullName;
    }

    public String getProfessorUsername() {
        return professorUsername;
    }

    public void setProfessorUsername(String professorUsername) {
        this.professorUsername = professorUsername;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", objectives='" + objectives + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
