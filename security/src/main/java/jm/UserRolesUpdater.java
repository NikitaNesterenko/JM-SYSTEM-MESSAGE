package jm;

import jm.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserRolesUpdater {

    private UserService userService;
    private WorkspaceUserRoleService workspaceUserRoleService;

    @Autowired
    public void setUserRolesUpdater(WorkspaceUserRoleService workspaceUserRoleService) {
        this.workspaceUserRoleService = workspaceUserRoleService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void update(Long workspaceId) {

        Set<GrantedAuthority> authorities = new HashSet<>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        jm.model.User user = userService.getUserByLogin(auth.getName());

        Set<Role> userRoles = workspaceUserRoleService.getRole(workspaceId, user.getId());

        Set<GrantedAuthority> actualAuthorities = userRoles.stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getAuthority())).collect(Collectors.toSet());

        authorities.forEach(role -> actualAuthorities.add(new SimpleGrantedAuthority(role.getAuthority())));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getName(), auth.getCredentials(), actualAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
