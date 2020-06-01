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
    private final String WITHOUT_WORKSPACE = "ROLE_REGISTERED";

    private HttpServletRequest httpServletRequest;
    private UserService userServiceImpl;
    private WorkspaceUserRoleService workspaceUserRoleService;

    @Autowired
    public UserDetailsServiceImpl(UserService userServiceImpl, WorkspaceUserRoleService workspaceUserRoleService, HttpServletRequest httpServletRequest) {
        this.userServiceImpl = userServiceImpl;
        this.workspaceUserRoleService = workspaceUserRoleService;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public UserDetails loadUserByUsername(String workspaceLogin) throws UsernameNotFoundException {
        HttpSession httpSession = httpServletRequest.getSession(true);
        Workspace workspace = (Workspace) httpSession.getAttribute("WorkspaceID");

        User user = userServiceImpl.getUserByLogin(workspaceLogin);
        org.springframework.security.core.userdetails.User.UserBuilder builder;
        if (user != null) {
            builder = org.springframework.security.core.userdetails.User.withUsername(workspaceLogin);
            builder.password(user.getPassword());
            if (workspace != null) {
                Set<Role> authorities = workspaceUserRoleService.getRole(workspace.getId(), user.getId());
                builder.authorities(authorities);
                logger.info("User " + workspaceLogin + " logged in " + workspace.getName() + " with roles: " + authorities);
            } else {
                builder.authorities(WITHOUT_WORKSPACE);
                logger.info("User " + workspaceLogin + "logged in JM-SYSTEM-MESSAGE with roles: " + WITHOUT_WORKSPACE);
            }
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }
}
