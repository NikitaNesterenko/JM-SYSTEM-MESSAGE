package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jm.NotificationService;
import jm.component.Response;
import jm.model.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * контролер для вкл/выкл уведомлений и их создание
 */
@RestController
@RequestMapping(value = "/rest/api/notifications")
public class NotificationRestController {

    private NotificationService service;

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationRestController.class);

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
        logger.info("test 6");
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
        logger.info("test 7");
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
        logger.info("test 8");
        return Response.ok().build();
    }
}
