package ApplicationManagmentApp.ApplicationManagmentApp.service;

import ApplicationManagmentApp.ApplicationManagmentApp.model.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
@Service
public interface ProfessorService {

    public void retrieveProfile(String username);

    public void saveProfile(Professor professor);

    public List<Subject> listProfessorSubjects();

    public void addSubject(Subject subject);

    public List<Application> listApplications(int subjectId);

    public List<Thesis> listProfessorThesis();

    public void assignSubject(String strategy, double fewestCourses, double bestAvgGrade);

    public void assignSubject(Student bestApplicant, Subject subject, String strategy, double fewestCourses, double bestAvgGrade); // Method created for test

    public void deleteSubject(int subjectId);

    public void calculateFinalThesisGrade(Thesis updatedThesis);

    public Professor getMyProfessor();

    public void setMyProfessor(Professor professor);

    public void updatePersonalInformation(Professor updatedProfessor);

    public Subject findSubjectById(int id);

    public void setMyAssignSubjectId(int assignSubjectId);

    public Thesis findThesisById(int thesisId);

    public void updateThesisGrades(Thesis updatedThesis);

    public void deleteThesis(int thesisId);
}
