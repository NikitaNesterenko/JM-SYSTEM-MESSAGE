package jm.controller.rest;

import jm.WorkspaceAppService;
import jm.WorkspaceService;
import jm.model.WorkspaceApp.WorkspaceApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/app")
public class WorkspaceAppRestController {
    @Autowired
    WorkspaceAppService workspaceAppService;
    @Autowired
    WorkspaceService workspaceService;

    @PostMapping("/createApp")
    public ResponseEntity createApp(@RequestBody String[] appAndWorkspaceName) {
        WorkspaceApp workspaceApp = new WorkspaceApp(appAndWorkspaceName[0], workspaceService.getWorkspaceByName(appAndWorkspaceName[1]));
        workspaceAppService.createWorkspaceApp(workspaceApp);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/{appId}/slash-commands")
    public ResponseEntity setSlashCommands(@PathVariable Long appId, @RequestBody Map<String, String> map) {
        WorkspaceApp app = workspaceAppService.getWorkspaceAppById(appId);
        Map<String, String> slashMap = app.getSlashCommands();
        slashMap.putAll(map);
        app.setSlashCommands(slashMap);
        workspaceAppService.updateWorkspaceApp(app);
        return new ResponseEntity(HttpStatus.OK);
    }

}
