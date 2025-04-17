package ApplicationManagmentApp.ApplicationManagmentApp.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="professor")
public class Professor {
    @Id
    @Column(name="username")
    private String username;
    @OneToMany(fetch=FetchType.EAGER ,cascade=CascadeType.ALL)
    @JoinColumn(name="professorusername")
    private List<Thesis>thesisList;
    @Column(name="fullname")
    private String fullName;
    @Column(name="speciality")
    private String speciality;

    public Professor(){

    }

    public Professor(String username) {
        this.username =username;
    }

    public Professor(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
    }

    public void setThesisList(List<Thesis> thesisList) {
        this.thesisList = thesisList;
    }

    public void addThesis(Thesis thesis){
        this.thesisList.add(thesis);
    }

    public List<Thesis> getThesisList() {
        return thesisList;
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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", speciality='" + speciality + '\'' +
                '}';
    }
}
