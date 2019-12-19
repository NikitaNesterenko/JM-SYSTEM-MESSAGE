package jm.controller.rest;

import jm.UserService;
import jm.WorkspaceService;
import jm.WorkspaceUserRoleService;
import jm.model.User;
import jm.model.Workspace;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/workspaces")
public class WorkspaceRestController {

    private WorkspaceService workspaceService;
    private WorkspaceUserRoleService workspaceUserRoleService;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Autowired
    public void setWorkspaceUserRoleService(WorkspaceUserRoleService workspaceUserRoleService) {
        this.workspaceUserRoleService = workspaceUserRoleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(workspaceService.getWorkspaceById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity createWorkspace(@RequestBody Workspace workspace) {
        try {
            workspaceService.createWorkspace(workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateChannel(@RequestBody Workspace workspace) {
        try {
            workspaceService.updateWorkspace(workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteWorkspace(@PathVariable("id") Long id) {
        workspaceService.deleteWorkspace(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Workspace>> getAllWorkspaces() {
        return new ResponseEntity<>(workspaceService.gelAllWorkspaces(),HttpStatus.OK);
    }

    @GetMapping("/choosed")
    public ResponseEntity<Workspace> getChoosedWorkspace(HttpServletRequest request) {
       Workspace workspace = (Workspace) request.getSession().getAttribute("WorkspaceID");
        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

    @GetMapping("/choosed/{name}")
    public ResponseEntity<Boolean> choosedWorkspace(@PathVariable("name") String name, HttpServletRequest request) {
        Workspace workspace = workspaceService.getWorkspaceByName(name);
        if (workspace == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        request.getSession().setAttribute("WorkspaceID", workspace);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Boolean> getWorkspaceByName(@PathVariable("name") String name, HttpServletRequest request) {
        return choosedWorkspace(name, request);
    }

    @GetMapping("/byLoggedUser")
    public ResponseEntity<List<Workspace>> getAllWorkspacesByUser(Principal principal) {
        String name = principal.getName();
        User user = userService.getUserByLogin(name);
        return new ResponseEntity<>(workspaceService.getWorkspacesByUser(user), HttpStatus.OK);
    }
}
