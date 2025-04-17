package ApplicationManagmentApp.ApplicationManagmentApp.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name="Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private  int id;

    @Column(name="user_name",unique = true)
    private String username;

    @Column(name="password")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        SimpleGrantedAuthority authority= new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getPassword(){
        return  password;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonLocked(){
        return  true;
    }

    @Override
    public boolean isAccountNonExpired(){
        return  true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return  true;
    }

    @Override
    public boolean isEnabled(){
        return  true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + username + '\'' +
                ", passWord='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}