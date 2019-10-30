package jm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor

@Entity
@Table(name = "createWorkspaceToken")
public class CreateWorkspaceToken {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspaceName")
    private String workspaceName;

    @Column(name = "userEmail")
    private String userEmail;

    @Column(name = "channelName")
    private String channelname;

    @Column(name = "code", nullable = false)
    private int code;

    public CreateWorkspaceToken (int code) {
        this.code = code;
    }

}
