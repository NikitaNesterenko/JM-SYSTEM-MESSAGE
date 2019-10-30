package jm;

import jm.model.Role;
import jm.model.User;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

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
        String[] strings = workspaceLogin.split(":");
        String workspaceName = null;
        String login = null;
        if (strings.length > 1) {
            workspaceName = strings[0];
            login = strings[1];
        } else if (strings.length == 1) {
            login = strings[0];
        }
        User user = userServiceImpl.getUserByLogin(login);
        org.springframework.security.core.userdetails.User.UserBuilder builder = null;
        if (user != null) {
            Workspace workspace = workspaceService.getWorkspaceByName(workspaceName);
            builder = org.springframework.security.core.userdetails.User.withUsername(login);
            builder.password(user.getPassword());
            if (workspace != null) {
                Set<Role> authorities = workspaceUserRoleService.getRole(workspace, user);
                builder.authorities(authorities);
                System.out.println(authorities);
            } else {
                builder.authorities("REGISTERED");
                System.out.println("REGISTERED");
            }
        } else {
            throw new UsernameNotFoundException("User not found.");
        }
        return builder.build();
    }
}
