//package jm.config.inititalizer;
//
//import jm.UserService;
//import jm.WorkspaceService;
//import jm.WorkspaceUserRoleService;
//import jm.api.dao.RoleDAO;
//import jm.model.Role;
//import jm.model.User;
//import jm.model.Workspace;
//import jm.model.WorkspaceUserRole;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class TestDataSecurityInitializer {
//    private static final Logger logger = LoggerFactory.getLogger(TestDataSecurityInitializer.class);
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private RoleDAO roleDAO;
//    @Autowired
//    private WorkspaceService workspaceService;
//    @Autowired
//    private WorkspaceUserRoleService workspaceUserRoleService;
//
//    private void init() {
//        logger.info("Data init has been started!!!");
//        dataInit();
//        logger.info("Data init has been done!!!");
//    }
//
//    private void dataInit() {
//        List<Role> roles = createRoles();
//        List<User> users =  createUsers();
//        List<Workspace> workspaces = createWorkspaces();
//        createLinkRoles(workspaces, users, roles);
//    }
//
//    private List<Role> createRoles() {
//        List<Role> roles = new ArrayList<>();
//
//        Role userRole = new Role();
//        userRole.setRole("ROLE_USER");
//        roleDAO.persist(userRole);
//
//        Role ownerRole = new Role();
//        ownerRole.setRole("ROLE_OWNER");
//        roleDAO.persist(ownerRole);
//
//        roles.add(userRole);
//        roles.add(ownerRole);
//
//        return roles;
//    }
//
//    private List<User> createUsers() {
//        List<User> users = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            User user = new User();
//            user.setName("name-" + i);
//            user.setLastName("lastname-" + i);
//            user.setEmail("email-" + i + "@service.com");
//            user.setLogin("login-" + i);
//            user.setPassword("pass-" + i);
//            users.add(user);
//        }
//        for (User user: users) {
//            userService.createUser(user);
//        }
//        return users;
//    }
//
//    private List<Workspace> createWorkspaces() {
//        List<Workspace> workspaces = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            Workspace workspace = new Workspace();
//            workspace.setName("workspace-" + i);
//            workspace.setIsPrivate(false);
//            workspace.setCreatedDate(LocalDateTime.now());
//            workspaces.add(workspace);
//        }
//        for (Workspace workspace: workspaces) {
//            workspaceService.createWorkspace(workspace);
//        }
//        return workspaces;
//    }
//
//    private void createLinkRoles(List<Workspace> workspaces,
//                             List<User> users,
//                             List<Role> roles) {
//        List<WorkspaceUserRole> workspaceUserRoles = new ArrayList<>();
//        // Назначаю четверым из пяти юзеров роль пользователя во всех воркспейсах
//        for (int i = 0; i < users.size() - 1; i++) {
//            User user = users.get(i);
//            Role role = roles.get(0);
//            for (int j = 0; j < workspaces.size(); j++) {
//                WorkspaceUserRole workspaceUserRole = new WorkspaceUserRole();
//                workspaceUserRole.setWorkspace(workspaces.get(j));
//                workspaceUserRole.setUser(user);
//                workspaceUserRole.setRole(role);
//                workspaceUserRoles.add(workspaceUserRole);
//            }
//        }
//        // Назначаю права админа воркспейса юзерам (при равенстве id юзера и воркспейса юзер становится админом)
//        for (int i = 0; i < workspaces.size(); i++) {
//            User user = users.get(i);
//            Workspace workspace = workspaces.get(i);
//            Role role = roles.get(1);
//            WorkspaceUserRole workspaceUserRole = new WorkspaceUserRole();
//            workspaceUserRole.setWorkspace(workspace);
//            workspaceUserRole.setUser(user);
//            workspaceUserRole.setRole(role);
//            workspaceUserRoles.add(workspaceUserRole);
//        }
//
//        for (WorkspaceUserRole workspaceUserRole: workspaceUserRoles) {
//            workspaceUserRoleService.create(workspaceUserRole);
//        }
//
//
//    }
//
//}
