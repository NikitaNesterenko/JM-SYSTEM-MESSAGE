package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.UserService;
import jm.WorkspaceService;
import jm.WorkspaceUserRoleService;
import jm.dto.WorkspaceDTO;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Autowired
    public void setWorkspaceUserRoleService(WorkspaceUserRoleService workspaceUserRoleService) {
        this.workspaceUserRoleService = workspaceUserRoleService;
    }

    @GetMapping("/{id}")
    @Operation(
            operationId = "getWorkspaceById",
            summary = "Get workspace by id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            ),
                            description = "OK: get workspace"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no workspace with such id")
            })
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable("id") Long id) {
        final Workspace workspace = workspaceService.getWorkspaceById(id);
        return workspace != null
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/create")
    @Operation(
            operationId = "createWorkspace",
            summary = "Create workspace",
            responses = {
                    @ApiResponse(
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Workspace.class)
                            )
                    ),
                    @ApiResponse(responseCode = "200", description = "ОК: workspace created"),
                    @ApiResponse(responseCode = "400", description = "BAD_REQUEST: unable to create workspace")
            })
    public ResponseEntity<?> createWorkspace(@RequestBody Workspace workspace) {
        try {
            workspaceService.createWorkspace(workspace);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateChannel",
            summary = "Update channel",
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
    public ResponseEntity<?> updateChannel(@RequestBody WorkspaceDTO workspace, HttpServletRequest request) {
        try {
            workspaceService.updateWorkspace(workspaceService.getWorkspaceById(workspace.getId()));
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
        request.getSession(false).setAttribute("WorkspaceID", workspace);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            operationId = "deleteWorkspace",
            summary = "Delete workspace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: workspace deleted"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no workspace with such id")
            })
    public ResponseEntity<?> deleteWorkspace(@PathVariable("id") Long id) {
        if (getWorkspaceById(id).getStatusCode().is2xxSuccessful()) {
            workspaceService.deleteWorkspace(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    @Operation(
            operationId = "getAllWorkspaces",
            summary = "Get all workspaces",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Workspace.class)
                            ),
                            description = "OK: get workspaces"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no workspaces")
            })
    public ResponseEntity<List<Workspace>> getAllWorkspaces() {
        final List<Workspace> workspaces = workspaceService.getAllWorkspaces();
        return workspaces != null && !workspaces.isEmpty()
                ? ResponseEntity.ok(workspaces)
                : ResponseEntity.badRequest().build();
    }

    @GetMapping("/chosen")
    @Operation(
            operationId = "getChosenWorkspace",
            summary = "Get chosen workspace",
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
    public ResponseEntity<WorkspaceDTO> getChosenWorkspace(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WorkspaceDTO workspace = (WorkspaceDTO) request.getSession(false).getAttribute("WorkspaceID");
        if (workspace == null) {
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).header(HttpHeaders.LOCATION, "/chooseWorkspace").build();
        }
        return ResponseEntity.ok(workspace);
    }

    @GetMapping("/chosen/{name}")
    @Operation(
            operationId = "chosenWorkspace",
            summary = "Set chosen workspace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: get workspace"),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: unable to find workspace")
            })
    public ResponseEntity<Boolean> chosenWorkspace(@PathVariable("name") String name, HttpServletRequest request) {
        if (getWorkspaceByName(name, request).getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().build();

    }

    @GetMapping("/name/{name}")
    @Operation(
            operationId = "getWorkspaceByName",
            summary = "Get workspace by name",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            ),
                            description = "OK: got workspace"
                    )
            })
    public ResponseEntity<Boolean> getWorkspaceByName(@PathVariable("name") String name, HttpServletRequest request) {
        WorkspaceDTO workspace = workspaceService.getWorkspaceDTOByName(name).get();
        if (workspace != null) {
            request.getSession(true).setAttribute("WorkspaceID", workspace);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/byLoggedUser")
    @Operation(
            operationId = "getAllWorkspacesByUser",
            summary = "Get all workspace by user",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = Workspace.class)
                            ),
                            description = "OK: get workspaces"
                    ),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no workspaces at this user")
            })
    public ResponseEntity<List<Workspace>> getAllWorkspacesByUser(Principal principal) {
        // TODO: ПЕРЕДЕЛАТЬ получать только UserID, остальная информация о юзере не используется
        final List<Workspace> list = workspaceService.getWorkspacesByUserId(userService.getUserByLogin(principal.getName()).getId());
        return list != null && !list.isEmpty()
                ? ResponseEntity.ok(list)
                : ResponseEntity.badRequest().build();
    }
}
