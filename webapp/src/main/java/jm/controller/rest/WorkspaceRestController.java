package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.UserService;
import jm.WorkspaceService;
import jm.WorkspaceUserRoleService;
import jm.component.Response;
import jm.dto.WorkspaceDTO;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
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
    public Response<Workspace> getWorkspaceById(@PathVariable("id") Long id) {
        final Workspace workspace = workspaceService.getWorkspaceById(id);
        return workspace != null
                ? Response.ok().build()
                : Response.error(HttpStatus.BAD_REQUEST, "No workspace with such id ");
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
    public Response<?> createWorkspace(@RequestBody Workspace workspace) {
        try {
            workspaceService.createWorkspace(workspace);
            return Response.ok().build();
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return Response.error(HttpStatus.BAD_REQUEST, "Unable to create workspace");
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
    public Response<?> updateChannel(@RequestBody WorkspaceDTO workspace, HttpServletRequest request) {
        try {
            workspaceService.updateWorkspace(workspaceService.getWorkspaceById(workspace.getId()));
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return Response.error(HttpStatus.BAD_REQUEST, "Unable to update channel");
        }
        request.getSession(false).setAttribute("WorkspaceID",  workspace); //Store of non serializable object into HttpSession не придумал решение
        return Response.ok().build();
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(
            operationId = "deleteWorkspace",
            summary = "Delete workspace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: workspace deleted"),
                    @ApiResponse(responseCode = "404", description = "NOT_FOUND: no workspace with such id")
            })
    public Response<?> deleteWorkspace(@PathVariable("id") Long id) {
        if (getWorkspaceById(id).getStatusCode().is2xxSuccessful()) {
            workspaceService.deleteWorkspace(id);
            return Response.ok().build();
        }
        return Response.error(HttpStatus.BAD_REQUEST, "No workspace with such id");
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
    public Response<List<Workspace>> getAllWorkspaces() {
        final List<Workspace> workspaces = workspaceService.getAllWorkspaces();
        return workspaces != null && !workspaces.isEmpty()
                ? Response.ok(workspaces)
                : Response.error(HttpStatus.BAD_REQUEST, "No workspaces");
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
    public Response<WorkspaceDTO> getChosenWorkspace(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WorkspaceDTO workspace = (WorkspaceDTO) request.getSession(false).getAttribute("WorkspaceID");
        if (workspace == null) {
            return Response.ok(HttpStatus.PERMANENT_REDIRECT).header(HttpHeaders.LOCATION, "/chooseWorkspace").build();
        }
        return Response.ok(workspace);
    }

    @GetMapping("/chosen/{name}")
    @Operation(
            operationId = "chosenWorkspace",
            summary = "Set chosen workspace",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: get workspace"),
                    @ApiResponse(responseCode = "400", description = "NOT_FOUND: unable to find workspace")
            })
    public Response<Boolean> chosenWorkspace(@PathVariable("name") String name, HttpServletRequest request) {
        if (getWorkspaceByName(name, request).getStatusCode().is2xxSuccessful()) {
            return Response.ok(true);
        }
        return Response.error(HttpStatus.BAD_REQUEST, "Unable to find workspace");
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
    public Response<Boolean> getWorkspaceByName(@PathVariable("name") String name, HttpServletRequest request) {
        WorkspaceDTO workspace = workspaceService.getWorkspaceDTOByName(name).get();
        if (workspace != null) {
            request.getSession(true).setAttribute("WorkspaceID", workspace);
            return Response.ok(true);
        }
        return Response.error(HttpStatus.BAD_REQUEST, "Error get workspace by name");
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
    public Response<List<Workspace>> getAllWorkspacesByUser(Principal principal) {
        // TODO: ПЕРЕДЕЛАТЬ получать только UserID, остальная информация о юзере не используется
        final List<Workspace> list = workspaceService.getWorkspacesByUserId(userService.getUserByLogin(principal.getName()).getId());
        return list != null && !list.isEmpty()
                ? Response.ok(list)
                : Response.error(HttpStatus.BAD_REQUEST, "No workspaces at this user");
    }
}
