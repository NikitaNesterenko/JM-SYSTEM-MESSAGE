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
import jm.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private static final Logger logger =
            LoggerFactory.getLogger(WorkspaceRestController.class);

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
        logger.info("test 24");
        return workspace != null
                ? Response.ok().build()
                : Response.error(HttpStatus.BAD_REQUEST).build();
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
            logger.info("test 24");
            return Response.ok().build();
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return Response.error(HttpStatus.BAD_REQUEST).build();
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
    public Response<?> updateChannel(@RequestBody Workspace workspace, HttpServletRequest request) {
        try {
            workspaceService.updateWorkspace(workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return Response.error(HttpStatus.BAD_REQUEST).build();
        }
        request.getSession(false).setAttribute("WorkspaceID", workspace);
        logger.info("test 25");
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
            logger.info("test 26");
            return Response.ok().build();
        }
        return Response.error(HttpStatus.BAD_REQUEST).build();
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
        logger.info("test 27");
        return workspaces != null && !workspaces.isEmpty()
                ? Response.ok(workspaces)
                : Response.error(HttpStatus.BAD_REQUEST).build();
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
    public Response<Workspace> getChosenWorkspace(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Workspace workspace = (Workspace) request.getSession(false).getAttribute("WorkspaceID");
        if (workspace == null) {
            return Response.ok(HttpStatus.PERMANENT_REDIRECT).header(HttpHeaders.LOCATION, "/chooseWorkspace").build();
        }
        logger.info("test 28");
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
        Workspace workspace = workspaceService.getWorkspaceByName(name);
        if (workspace == null) {
            return Response.error(HttpStatus.BAD_REQUEST).build();
        }
        request.getSession(true).setAttribute("WorkspaceID", workspace);
        logger.info("test 29");
        return Response.ok(true);
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
        if (chosenWorkspace(name, request).getStatusCode().is2xxSuccessful()) {
            chosenWorkspace(name, request);
            logger.info("test 30");
            return Response.ok(true);
        }
        return Response.error(HttpStatus.BAD_REQUEST).build();
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
        logger.info("test 31");
        return list != null && !list.isEmpty()
                ? Response.ok(list)
                : Response.error(HttpStatus.BAD_REQUEST).build();
    }
}
