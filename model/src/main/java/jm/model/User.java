package jm.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Blob;

import java.util.HashSet;

import java.util.Collection;

import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "avatar")
    private Blob avatar;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Set<WorkspaceUserRoleLink> workspaceUserRoleLink = new HashSet<>();

    public User() {
    }

    public User(String name, String lastName, String login, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Blob getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        this.avatar = avatar;
    }

    public Set<WorkspaceUserRoleLink> getWorkspaceUserRoleLink() {
        return this.workspaceUserRoleLink;
    }

    public void setWorkspaceUserRoleLink(Set<WorkspaceUserRoleLink> workspaceUserRoleLink) {
        this.workspaceUserRoleLink = workspaceUserRoleLink;
    }


    /*
        public Set<Role> getRoles() {
            return roles;
        }

        public void setRoles(Set<Role> roles) {
            this.roles = roles;
        }
    */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection roles = new HashSet();

        this.getWorkspaceUserRoleLink().forEach(x -> roles.add(x.getRole()));

        return (Collection<? extends GrantedAuthority>) roles;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.login;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                name.equals(user.name) &&
                lastName.equals(user.lastName) &&
                login.equals(user.login) &&
                email.equals(user.email) &&
                password.equals(user.password) &&
                avatar.equals(user.avatar); //&&
        //  roles.equals(user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, login, email, password, avatar);
        //, roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatar=" + avatar +
                '}';
    }
}
