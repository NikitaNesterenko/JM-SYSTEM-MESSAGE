package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jm.NotificationService;
import jm.component.Response;
import jm.model.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * контролер для вкл/выкл уведомлений и их создание
 */
@RestController
@RequestMapping(value = "/rest/api/notifications")
@Tag(name = "notifications", description = "Notification API")
public class NotificationRestController {

    private NotificationService service;

    @Autowired
    public void setService(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/{workspaceId}/{userId}")
    @Operation(
            operationId = "getNotifications",
            summary = "Get notification",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Notifications.class)
                            ),
                            description = "OK: get notification"
                    )
            })
    public Response<Notifications> getNotifications(@PathVariable Long workspaceId, @PathVariable Long userId) {
        return Response.ok(service.getNotification(userId, workspaceId));
    }

    @PostMapping(value = "/create")
    @Operation(
            operationId = "addNotifications",
            summary = "add notification",
            responses = {
                    @ApiResponse(responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Notifications.class)
                            ),
                            description = "OK: add notification"
                    )
            })
    public Response<Notifications> addNotifications(@RequestBody Notifications notifications) {
        service.addNotification(notifications);
        return Response.ok().build();
    }

    @PutMapping(value = "/update")
    @Operation(
            operationId = "updateNotifications",
            summary = "update notification",
            responses = {
                    @ApiResponse(responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Notifications.class)
                            ),
                            description = "OK: update notification"
                    )
            })
    public Response<Notifications> updateNotifications(@RequestBody Notifications notifications) {
        service.updateNotification(notifications);
        return Response.ok().build();
    }
}
