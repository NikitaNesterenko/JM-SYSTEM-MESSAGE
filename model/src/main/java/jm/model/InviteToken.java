package jm.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "invite_tokens")
public class InviteToken {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "hash", nullable = false)
    private String hash;

    @OneToOne(targetEntity = Workspace.class)
    @JoinColumn(name = "workspace_id"/*, nullable = false*/)
    private Workspace workspace;

    public InviteToken() {
    }

    public InviteToken(String email, String firstName, String lastName, String hash, Workspace workspace) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hash = hash;
        this.workspace = workspace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash() {
        this.hash = Integer.toString(hashCode());
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InviteToken that = (InviteToken) o;
        return id.equals(that.id) &&
                email.equals(that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                hash.equals(that.hash) &&
                workspace.equals(that.workspace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "InviteToken{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", hash='" + hash + '\'' +
                ", workspace=" + workspace +
                '}';
    }
}
