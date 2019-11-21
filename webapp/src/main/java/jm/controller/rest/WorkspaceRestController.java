package jm.controller.rest;

import jm.UserService;
import jm.WorkspaceService;
import jm.model.User;
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    private static final Logger logger = LoggerFactory.getLogger(
            WorkspaceRestController.class);

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable("id") Long id) {
        logger.info("Workspace с id = {}", id);
        logger.info(workspaceService.getWorkspaceById(id).toString());
        return new ResponseEntity<>(workspaceService.getWorkspaceById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Workspace> createWorkspace(@RequestBody Workspace workspace) {
        try {
            workspaceService.createWorkspace(workspace);
            logger.info("Созданный workspace : {}", workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            logger.warn("Не удалось создать workspace");
            ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateWorkspace(@RequestBody Workspace workspace) {
        Workspace existingWorkspace = workspaceService.getWorkspaceById(workspace.getId());
        if (existingWorkspace == null) {
            logger.warn("Workspace не найден");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            workspaceService.updateWorkspace(workspace);
            logger.info("Обновленный workspace: {}", workspace);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteWorkspace(@PathVariable("id") Long id) {
        workspaceService.deleteWorkspace(id);
        logger.info("Удален workspace с id = {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Workspace>> getAllWorkspaces() {
        logger.info("Список workspaces : ");
        for (Workspace workspace : workspaceService.gelAllWorkspaces()) {
            logger.info(workspace.toString());
        }
        return new ResponseEntity<>(workspaceService.gelAllWorkspaces(), HttpStatus.OK);
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

    @GetMapping("/byLoggedUser")
    public ResponseEntity<List<Workspace>> getAllWorkspacesByUser(Principal principal) {
        String name = principal.getName();
        User user = userService.getUserByLogin(name);
        return new ResponseEntity<>(workspaceService.getWorkspacesByOwner(user), HttpStatus.OK);
    }
}
