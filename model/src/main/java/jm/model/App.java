package jm.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "apps")
public class App {

    public static final String JAVA_MENTOR_BOT = "Java Mentor Bot";
    public static final String GOOGLE_DRIVE = "Google Drive";
    public static final String GITHUB = "GitHub";
    public static final String CODE_STREAM = "CodeStream";
    public static final String GOOGLE_CALENDAR = "Google calendar";
    public static final String ZOOM = "Zoom";
    public static final String SOCOCO = "Sococo";
    public static final String GITHUB_ENTERPRISE_SERVER = "GitHub Enterprise Server";
    public static final String GOOGLE_HANGOUTS = "Google+ Hangouts";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app_name")
    private String name;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    public App() {
    }
}
