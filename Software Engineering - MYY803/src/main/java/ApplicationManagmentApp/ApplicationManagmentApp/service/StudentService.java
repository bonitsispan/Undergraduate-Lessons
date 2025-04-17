package ApplicationManagmentApp.ApplicationManagmentApp.service;

import ApplicationManagmentApp.ApplicationManagmentApp.model.Student;
import ApplicationManagmentApp.ApplicationManagmentApp.model.Subject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {

    public void saveProfile(Student student);

    public void retrieveProfile(String username);

    public List<Subject> getAvailableSubjects();

    public void applyToSubject(int subjectId);

    public Subject getSubjectInformation(int subjectId);

    public Student getMyStudent();

    public void setMyStudent(Student myStudent);

    public void updatePersonalInformation(Student updatedStudent);

}
