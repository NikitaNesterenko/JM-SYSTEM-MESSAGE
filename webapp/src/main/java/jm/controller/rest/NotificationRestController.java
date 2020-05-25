package jm.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jm.NotificationService;
import jm.model.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * контролер для вкл/выкл уведомлений и их создание
 */
@RestController
@RequestMapping(value = "/rest/api/notifications")
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
    public ResponseEntity<Notifications> getNotifications(@PathVariable Long workspaceId, @PathVariable Long userId) {
        return  ResponseEntity.ok(service.getNotification(userId, workspaceId));
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
    public ResponseEntity<Notifications> addNotifications(@RequestBody Notifications notifications) {
        service.addNotification(notifications);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Notifications> updateNotifications(@RequestBody Notifications notifications) {
        service.updateNotification(notifications);
        return ResponseEntity.ok().build();
    }
}
