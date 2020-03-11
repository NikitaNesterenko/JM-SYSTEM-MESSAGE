package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.UserService;
import jm.WorkspaceService;
import jm.WorkspaceUserRoleService;
import jm.model.User;
import jm.model.Workspace;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/workspaces")
@Tag(name = "workspace", description = "Workspace API")
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
    @Operation(summary = "Get workspace by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            ),
                            description = "OK: get workspace"
                    )
            })
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(workspaceService.getWorkspaceById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create workspace",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: workspace created"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: unable to create workspace")
            })
    public ResponseEntity createWorkspace(@RequestBody Workspace workspace) {
        try {
            workspaceService.createWorkspace(workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/update")
    @Operation(summary = "Update channel",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "OK: channel updated"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: unable to update channel")
            })
    public ResponseEntity updateChannel(@RequestBody Workspace workspace) {
        try {
            workspaceService.updateWorkspace(workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete workspace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: workspace deleted")
            })
    public ResponseEntity deleteWorkspace(@PathVariable("id") Long id) {
        workspaceService.deleteWorkspace(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all workspaces",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Workspace.class)
                            ),
                            description = "OK: get workspaces"
                    )
            })
    public ResponseEntity<List<Workspace>> getAllWorkspaces() {
        return new ResponseEntity<>(workspaceService.getAllWorkspaces(),HttpStatus.OK);
    }

    @GetMapping("/chosen")
    @Operation(summary = "Get chosen workspace",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            ),
                            description = "OK: get workspace"
                    ),
                    @ApiResponse(responseCode = "308", description = "PERMANENT_REDIRECT: unable to find workspace")
            })
    public ResponseEntity<Workspace> getChosenWorkspace(HttpServletRequest request, HttpServletResponse response) throws IOException {
       Workspace workspace = (Workspace) request.getSession(false).getAttribute("WorkspaceID");
       if(workspace==null) {
           return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).header(HttpHeaders.LOCATION, "/chooseWorkspace").build();
       }
        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

    @GetMapping("/chosen/{name}")
    @Operation(summary = "Set chosen workspace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: get workspace"),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: unable to find workspace")
            })
    public ResponseEntity<Boolean> chosenWorkspace(@PathVariable("name") String name, HttpServletRequest request) {
        Workspace workspace = workspaceService.getWorkspaceByName(name);
        if (workspace == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        request.getSession(false).setAttribute("WorkspaceID", workspace);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Boolean> getWorkspaceByName(@PathVariable("name") String name, HttpServletRequest request) {
        return chosenWorkspace(name, request);
    }

    @GetMapping("/byLoggedUser")
    @Operation(summary = "Get all workspace by user",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Workspace.class)
                            ),
                            description = "OK: get workspaces"
                    )
            })
    public ResponseEntity<List<Workspace>> getAllWorkspacesByUser(Principal principal) {
        String name = principal.getName();
        User user = userService.getUserByLogin(name);
        List<Workspace> list = workspaceService.getWorkspacesByUserId(user.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
