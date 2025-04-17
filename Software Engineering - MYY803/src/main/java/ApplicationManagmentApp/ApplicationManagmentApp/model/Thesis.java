package ApplicationManagmentApp.ApplicationManagmentApp.model;

import javax.persistence.*;

@Entity
@Table(name="thesis")
public class Thesis {
    @Id
    @Column(name="id")
    private int id;
    @Column(name="topic")
    private String topic;
    @Column(name="implementationgrade")
    private double implementationGrade;
    @Column(name="reportgrade")
    private double reportGrade;
    @Column(name="presentationgrade")
    private double presentationGrade;

    @Column(name="finalgrade")
    private double finalGrade;

    @Column(name="professorusername")
    private String professorUsername;

    @Column(name="studentusername")
    private String studentUsername;

    @Column(name="studentfullname")
    private String studentFullName;

    public Thesis(){

    }

    public Thesis(int id) {
        this.id = id;
    }

    public Thesis(int id, String topic, String professorUsername, String studentUsername, String studentFullName) {
        this.id = id;
        this.topic = topic;
        this.professorUsername = professorUsername;
        this.studentUsername = studentUsername;
        this.studentFullName = studentFullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String studentFullName) {
        this.studentFullName = studentFullName;
    }

    public String getProfessorUsername() {
        return professorUsername;
    }

    public void setProfessorUsername(String professorUsername) {
        this.professorUsername = professorUsername;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public double getImplementationGrade() {
        return implementationGrade;
    }

    public void setImplementationGrade(double implementationGrade) {
        this.implementationGrade = implementationGrade;
    }

    public double getReportGrade() {
        return reportGrade;
    }

    public void setReportGrade(double reportGrade) {
        this.reportGrade = reportGrade;
    }

    public double getPresentationGrade() {
        return presentationGrade;
    }

    public void setPresentationGrade(double presentationGrade) {
        this.presentationGrade = presentationGrade;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(double finalGrade) {
        this.finalGrade = finalGrade;
    }

    @Override
    public String toString() {
        return "Thesis{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", implementationGrade=" + implementationGrade +
                ", reportGrade=" + reportGrade +
                ", presentationGrade=" + presentationGrade +
                '}';
    }

}
