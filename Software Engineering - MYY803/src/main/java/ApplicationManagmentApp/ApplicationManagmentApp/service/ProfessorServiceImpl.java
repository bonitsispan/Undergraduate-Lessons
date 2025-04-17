package ApplicationManagmentApp.ApplicationManagmentApp.service;

import ApplicationManagmentApp.ApplicationManagmentApp.dao.ApplicationDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.ProfessorDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.SubjectDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.dao.ThesisDAORepository;
import ApplicationManagmentApp.ApplicationManagmentApp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ProfessorServiceImpl implements ProfessorService{
    @Autowired
    private ProfessorDAORepository professorDAORepository;
    @Autowired
    private SubjectDAORepository subjectDAORepository;
    @Autowired
    private ApplicationDAORepository applicationDAORepository;
    @Autowired
    private ThesisDAORepository thesisDAORepository;
    private Professor myProfessor;
    private int myAssignSubjectId;


    public  ProfessorServiceImpl(){
        super();
    }
    @Autowired
    public ProfessorServiceImpl(ProfessorDAORepository professorDAORepository, SubjectDAORepository subjectDAORepository, ApplicationDAORepository applicationDAORepository, ThesisDAORepository thesisDAORepository) {
        this.professorDAORepository = professorDAORepository;
        this.subjectDAORepository = subjectDAORepository;
        this.applicationDAORepository = applicationDAORepository;
        this.thesisDAORepository = thesisDAORepository;
    }

    @Override
    @Transactional
    public void saveProfile(Professor professor){

        this.professorDAORepository.save(professor);
        this.myProfessor = professor;
    }

    @Override
    @Transactional
    public void retrieveProfile(String username){

        this.myProfessor =  professorDAORepository.findByUsername(username);
    }


    @Override
    @Transactional
    public void updatePersonalInformation(Professor updatedProfessor){

        this.myProfessor = updatedProfessor;

        List<Subject> subjects = subjectDAORepository.findByProfessorUsername(myProfessor.getUsername());
        for(Subject subject: subjects){
            subject.setProfessorFullName(myProfessor.getFullName());
        }

        professorDAORepository.save(myProfessor);
    }

    @Override
    public List<Subject> listProfessorSubjects() {

        return subjectDAORepository.findByProfessorUsername(myProfessor.getUsername());
    }

    @Override
    @Transactional
    public void addSubject(Subject subject){

        this.subjectDAORepository.save(subject);
    }

    @Override
    @Transactional
    public void deleteSubject(int subjectId){

        Subject subject = subjectDAORepository.findById(subjectId);

        List<Application> applicationList = subject.getApplicationList();
        for (Application application : applicationList) {
            this.applicationDAORepository.deleteById(application.getId());
        }

        this.subjectDAORepository.deleteById(subjectId);
    }

    @Override
    @Transactional
    public Subject findSubjectById(int id){

        return subjectDAORepository.findById(id);
    }

    @Override
    public List<Application> listApplications(int subjectId){

        return subjectDAORepository.findById(subjectId).getApplicationList();
    }

    @Override
    @Transactional
    public void assignSubject(String strategy, double fewestCourses, double bestAvgGrade){

        Subject subject = subjectDAORepository.findById(this.myAssignSubjectId);

        if(subject.getApplicationList().size() != 0){ // There are not any applications for this subject.
            Student bestApplicant=subject.findBestApplicant(strategy, fewestCourses, bestAvgGrade);

            if(bestApplicant.getYearOfStudies() != -1){ // if YearOfStudies of best applicant equals -1 means that there is not best applicant.
                Thesis newThesis = new Thesis(subject.getId(), subject.getTitle(), myProfessor.getUsername(), bestApplicant.getUsername(), bestApplicant.getFullName());
                this.myProfessor.addThesis(newThesis);
                this.professorDAORepository.save(myProfessor);
                deleteSubject(subject.getId());
            }
        }
    }

    @Override
    public List<Thesis> listProfessorThesis(){

        return this.thesisDAORepository.findByProfessorUsername(this.myProfessor.getUsername());
    }

    @Override
    public Thesis findThesisById(int thesisId){

        return this.thesisDAORepository.findById(thesisId);
    }

    @Override
    @Transactional
    public void updateThesisGrades(Thesis updatedThesis){

        this.thesisDAORepository.save(updatedThesis);
    }

    @Override
    @Transactional
    public void calculateFinalThesisGrade(Thesis updatedThesis){

        double finalGrade = updatedThesis.getImplementationGrade()*0.7+updatedThesis.getReportGrade()*0.15+updatedThesis.getPresentationGrade()*0.15;
        finalGrade = Math.round(finalGrade * 100.0) / 100.0;
        updatedThesis.setFinalGrade((finalGrade));

        this.thesisDAORepository.save(updatedThesis);
    }

    @Override
    @Transactional
    public void deleteThesis(int thesisId){

        this.thesisDAORepository.deleteById(thesisId);
    }

    @Override
    public void setMyAssignSubjectId(int assignSubjectId) {

        this.myAssignSubjectId = assignSubjectId;
    }

    @Override
    public Professor getMyProfessor(){

        return myProfessor;
    }

    @Override
    public void setMyProfessor(Professor professor){

        this.myProfessor=professor;
    }


    @Override
    @Transactional // Method created for test
    public void assignSubject(Student bestApplicant,Subject subject,String strategy, double fewestCourses, double bestAvgGrade){

        if(subject.getApplicationList().size() != 0){
            if(bestApplicant.getYearOfStudies() != -1){
                Thesis newThesis = new Thesis(subject.getId(), subject.getTitle(), myProfessor.getUsername(), bestApplicant.getFullName(), bestApplicant.getFullName());
                this.myProfessor.addThesis(newThesis);
                this.professorDAORepository.save(myProfessor);
                //deleteSubject(subject.getId());
            }
        }
    }

}
