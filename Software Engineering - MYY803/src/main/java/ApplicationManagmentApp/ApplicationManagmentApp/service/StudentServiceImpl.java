package ApplicationManagmentApp.ApplicationManagmentApp.service;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.ProfessorDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.StudentDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.SubjectDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.ThesisDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentDAORepository studentDAORepository;
    @Autowired
    private SubjectDAORepository subjectDAORepository;
    @Autowired
    private ThesisDAORepository thesisDAORepository;
    private Student myStudent;

    public StudentServiceImpl(){
        super();
    }

    @Autowired
    public StudentServiceImpl(StudentDAORepository studentDAORepository, SubjectDAORepository subjectDAORepository, ThesisDAORepository thesisDAORepository) {
        this.studentDAORepository = studentDAORepository;
        this.subjectDAORepository = subjectDAORepository;
        this.thesisDAORepository = thesisDAORepository;
    }

    @Override
    @Transactional
    public void saveProfile(Student student){

        this.studentDAORepository.save(student);
        this.myStudent = student;
    }

    @Override
    @Transactional
    public void retrieveProfile(String username){

        this.myStudent =  studentDAORepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void updatePersonalInformation(Student updatedStudent){

        this.myStudent = updatedStudent;

        List<Thesis> theses = this.thesisDAORepository.findByStudentUsername(this.myStudent.getUsername());
        for(Thesis thesis: theses){
            thesis.setStudentFullName(this.myStudent.getFullName());
        }

        studentDAORepository.save(myStudent);
    }

    @Override
    @Transactional
    public List<Subject> getAvailableSubjects(){

        return this.subjectDAORepository.findAll();
    }

    @Override
    @Transactional
    public void applyToSubject(int subjectId){

        Subject subject = subjectDAORepository.findById(subjectId);

        if(!(applicationExist(subject))){ // Check if the student has already applied

            Application newApplication = new Application();
            newApplication.setStudent(myStudent);
            newApplication.setSubjectId(subjectId);

            subject.addApplication(newApplication);
            this.subjectDAORepository.save(subject);
        }
    }

    private boolean applicationExist(Subject subject){

        List<Application> applications = subject.getApplicationList();
        if(applications.isEmpty()){
            return false;
        }

        String username = myStudent.getUsername();
        for(Application app: applications){
            if((app.getSubjectId() == subject.getId()) && (app.getStudent().getUsername().equals(username))){
                return true;
            }
        }
        return false;
    }


    @Override
    public Subject getSubjectInformation(int subjectId){

        return subjectDAORepository.findById(subjectId);
    }

    @Override
    public Student getMyStudent(){

        return myStudent;
    }

    @Override
    public void setMyStudent(Student myStudent) {

        this.myStudent = myStudent;
    }
}
