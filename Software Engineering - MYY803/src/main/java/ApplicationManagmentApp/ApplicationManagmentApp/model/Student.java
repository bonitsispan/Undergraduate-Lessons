package ApplicationManagmentApp.ApplicationManagmentApp.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="student")
public class Student {
    @Id
    @Column(name="username")
    private String username;

    @Column(name="fullname")
    private String fullName;

    @Column(name="yearofstudies")
    private int yearOfStudies;
    @Column(name="currentaveragearade")
    private double currentAverageGrade;
    @Column(name="numberofremainingcourses")
    private int numberOfRemainingCourses;


    public Student(){

    }

    public Student(String username) {
        this.username = username;
    }

    public Student(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getYearOfStudies() {
        return yearOfStudies;
    }

    public void setYearOfStudies(int yearOfStudies) {
        this.yearOfStudies = yearOfStudies;
    }

    public double getCurrentAverageGrade() {
        return currentAverageGrade;
    }

    public void setCurrentAverageGrade(double currentAverageGrade) {
        this.currentAverageGrade = currentAverageGrade;
    }

    public int getNumberOfRemainingCourses() {
        return numberOfRemainingCourses;
    }

    public void setNumberOfRemainingCourses(int numberOfRemainingCourses) {
        this.numberOfRemainingCourses = numberOfRemainingCourses;
    }

    @Override
    public String toString() {
        return "Full name: " + fullName  +
                ", Year of studies: " + yearOfStudies +
                ", Current average grade: " + currentAverageGrade +
                ", Number of remaining courses: " + numberOfRemainingCourses;
    }

}
