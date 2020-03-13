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
    public UserDetails loadUserByUsername(String workspaceLogin) throws UsernameNotFoundException {
        HttpSession httpSession = httpServletRequest.getSession(true);
        String workspaceName = (String) httpSession.getAttribute("workspaceName");
        if(workspaceName==null) workspaceName="workspace-0";

        User user = userServiceImpl.getUserByLogin(workspaceLogin);
        org.springframework.security.core.userdetails.User.UserBuilder builder = null;
        if (user != null) {
            builder = org.springframework.security.core.userdetails.User.withUsername(workspaceLogin);
            builder.password(user.getPassword());
            Workspace workspace = workspaceService.getWorkspaceByName(workspaceName);
            if (workspace != null) {
                Set<Role> authorities = workspaceUserRoleService.getRole(workspace.getId(), user.getId());
                builder.authorities(authorities);
            } else {
                builder.authorities(WITHOUT_WORKSPACE);
            }
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }
}
