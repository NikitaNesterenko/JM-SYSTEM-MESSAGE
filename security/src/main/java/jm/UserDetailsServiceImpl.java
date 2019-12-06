package jm;

import jm.model.Role;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final String WITHOUT_WORKSPACE = "REGISTERED";

    @Autowired
    HttpServletRequest httpServletRequest;

    private UserService userServiceImpl;
    private WorkspaceService workspaceService;
    private WorkspaceUserRoleService workspaceUserRoleService;

    public UserDetailsServiceImpl(UserService userServiceImpl, WorkspaceService workspaceService, WorkspaceUserRoleService workspaceUserRoleService) {
        this.userServiceImpl = userServiceImpl;
        this.workspaceService = workspaceService;
        this.workspaceUserRoleService = workspaceUserRoleService;
    }

    @Override
    public UserDetails loadUserByUsername(String workspaceEmail) throws UsernameNotFoundException {
        HttpSession httpSession = httpServletRequest.getSession(true);
        String workspaceName = (String) httpSession.getAttribute("workspaceName");
        if(workspaceName==null) workspaceName="workspace-0";
/*
        String[] strings = workspaceLogin.split(":");
        String workspaceName = null;
        String login = null;
        if (strings.length > 1) {
            workspaceName = strings[0];
            login = strings[1];
        } else if (strings.length == 1) {
            login = strings[0];
        }
*/
        User user = userServiceImpl.getUserByEmail(workspaceEmail);
        org.springframework.security.core.userdetails.User.UserBuilder builder = null;
        if (user != null) {
            builder = org.springframework.security.core.userdetails.User.withUsername(workspaceEmail);
            builder.password(user.getPassword());
            // if (workspaceName != null) {
            Workspace workspace = workspaceService.getWorkspaceByName(workspaceName);
            if (workspace != null) {
                Set<Role> authorities = workspaceUserRoleService.getRole(workspace, user);
                builder.authorities(authorities);
                System.out.println("User " + workspaceEmail + " logged in " + workspace.getName() + " with roles: " + authorities);
//                logger.info("User " + login + " logged in " + workspaceName + " with roles: " + authorities);
            } else {
                builder.authorities(WITHOUT_WORKSPACE);
                System.out.println("User " + workspaceEmail + "logged in JM-SYSTEM-MESSAGE with roles: " + WITHOUT_WORKSPACE);
//                logger.info("User " + login + "logged in JM-SYSTEM-MESSAGE with roles: " + WITHOUT_WORKSPACE);
            }
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }
}
