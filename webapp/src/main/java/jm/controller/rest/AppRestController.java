package jm.controller.rest;

import jm.AppsService;
import jm.model.App;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/rest/api/apps")
public class AppRestController {private AppsService appsService;

    @Autowired
    public AppRestController(AppsService appsService) {
        this.appsService = appsService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<App> getApp(@PathVariable("name") String appName, HttpServletRequest request) {
        Workspace workspace = (Workspace) request.getSession().getAttribute("WorkspaceID");
        App app = appsService.getAppByWorkspaceIdAndAppName(workspace.getId(), appName);
        if (app == null) {
            app = new App();
            app.setWorkspace(workspace);
            app.setName(appName);
            appsService.createApp(app);
        }
        return new ResponseEntity<>(app, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateApp(@RequestBody App app) {
        appsService.updateApp(app);
        return ResponseEntity.ok().build();
    }
}
