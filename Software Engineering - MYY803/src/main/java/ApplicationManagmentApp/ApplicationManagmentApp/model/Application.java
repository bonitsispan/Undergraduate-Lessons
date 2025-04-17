package ApplicationManagmentApp.ApplicationManagmentApp.model;

import javax.persistence.*;

@Entity
@Table(name="application")
public class Application {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @ManyToOne(fetch=FetchType.EAGER ,cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name="studentusername")
    private Student student;

    @Column(name="subjectid")
    private int subjectId;

    public Application() {

    }

    public Application(Student student, int subjectId) {
        this.student = student;
        this.subjectId = subjectId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                '}';
    }
}
