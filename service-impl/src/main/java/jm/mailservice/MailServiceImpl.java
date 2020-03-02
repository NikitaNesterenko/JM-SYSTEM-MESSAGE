package jm.mailservice;

import jm.InviteTokenService;
import jm.MailService;
import jm.UserService;
import jm.WorkspaceService;
import jm.model.CreateWorkspaceToken;
import jm.model.InviteToken;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    private JavaMailSender emailSender;
    private MailContentService mailContentService;
    private InviteTokenService inviteTokenService;
    private WorkspaceService workspaceService;
    private UserService userService;

    private String subjectMessage = "Recovery password from JM System Message";
    private String emailSubject = "Invite mail";
    private String urlSiteRecoveryPassword;
    private int charactersInHash;
    private Long validPasswordHours;
    private String emailSenderValue;

    public MailServiceImpl(JavaMailSender emailSender, MailContentService mailContentService,
                           InviteTokenService inviteTokenService, WorkspaceService workspaceService,
                           UserService userService,
                           @Value("${user.password.recovery.validHours : 24}") Long validPasswordHours,
                           @Value("${user.password.recovery.urlSite : http://localhost:8080/password-recovery/}") String urlSiteRecoveryPassword,
                           @Value("${user.password.recovery.charactersInHash : 10}") int charactersInHash,
                           @Value("${spring.mail.username}") String emailSenderValue) {
        this.emailSender = emailSender;
        this.mailContentService = mailContentService;
        this.inviteTokenService = inviteTokenService;
        this.workspaceService = workspaceService;
        this.userService = userService;
        this.validPasswordHours = validPasswordHours;
        this.urlSiteRecoveryPassword = urlSiteRecoveryPassword;
        this.charactersInHash = charactersInHash;
        this.emailSenderValue = emailSenderValue;
    }

    @Override
    public void sendInviteMessage(String nameFrom, String emailFrom, String emailTo,
                                  String workspace, String inviteLink) {
        String content = mailContentService.build(
                nameFrom,
                emailFrom,
                workspace,
                inviteLink);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(emailTo);
            messageHelper.setSubject(emailSubject);
            messageHelper.setText(content, true);
        };
        try {
            emailSender.send(messagePreparator);
            logger.info("Sending invitation to " + emailTo + " was successful");
        } catch (MailException e) {
            logger.error("Sending invitation to " + emailTo + " failed");
            e.printStackTrace();
        }

    }

    @Override
    public void sendInviteMessagesByTokenAndInvites(CreateWorkspaceToken createWorkspaceToken, String[] invites) {
        Arrays.stream(invites)
                .forEach(invite -> sendInviteMessage(
                        userService.getUserByEmail(createWorkspaceToken.getUserEmail()).getLogin(),
                        createWorkspaceToken.getUserEmail(),
                        invite,
                        createWorkspaceToken.getWorkspaceName(),
                        "http://localhost:8080/"));
    }

    @Override
    public Optional<CreateWorkspaceToken> sendConfirmationCode (String emailTo) {
        int code = (int) (Math.random() * 999999);
        String content = mailContentService.buildConfirmationCode(code);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(emailTo);
            messageHelper.setSubject("Confirmation code");
            messageHelper.setText(content, true);
        };

        CreateWorkspaceToken createWorkspaceToken = null;
        try {
            emailSender.send(messagePreparator);
            logger.info("Sending confirmation code to " + emailTo + " was successful");
            createWorkspaceToken = new CreateWorkspaceToken(code);
        } catch (MailException e) {
            logger.error("Sending confirmation code to " + emailTo + " failed");
            e.printStackTrace();
        }
        createWorkspaceToken.setUserEmail(emailTo);
        return Optional.ofNullable(createWorkspaceToken);
    }

    @Override
    public void sendRecoveryPasswordToken(User userTo) {
        LocalDateTime now = LocalDateTime.now();

        InviteToken inviteToken = new InviteToken();
        inviteToken.setEmail(userTo.getEmail());
        inviteToken.setHash(UUID.randomUUID().toString());

        //workspace нужен только для создания токена
        List<Workspace> workspacesByUser = workspaceService.getWorkspacesByUserId(userTo.getId());
        inviteToken.setWorkspace(workspacesByUser.get(0));
        inviteToken.setFirstName(userTo.getName());
        inviteToken.setLastName(userTo.getLastName());
        inviteToken.setDateCreate(now);
        inviteTokenService.createInviteToken(inviteToken);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(userTo.getEmail());
        simpleMailMessage.setSubject(subjectMessage);
        simpleMailMessage.setText(urlSiteRecoveryPassword + inviteToken.getHash());
        simpleMailMessage.setFrom(emailSenderValue);

        emailSender.send(simpleMailMessage);
    }

    @Override
    public boolean changePasswordUserByToken(String token, String password) {
            String[] split = token.split("/");

            InviteToken byHash = inviteTokenService.getByHash(split[4]);
            LocalDateTime validDateCreate = byHash.getDateCreate().plusHours(validPasswordHours);
            LocalDateTime now = LocalDateTime.now();

            if (validDateCreate.isAfter(now)) {
                User userByEmail = userService.getUserByEmail(byHash.getEmail());
                userByEmail.setPassword(password);
                userService.updateUser(userByEmail);
                inviteTokenService.deleteInviteToken(byHash.getId());
                logger.info("Восстановление пароля пользователя с id = {}", userByEmail.getId());
                return true;
            }

        logger.info("Попытка восстановления пароля пользователя с помощью токена = {}", token);
        return false;
    }

}
