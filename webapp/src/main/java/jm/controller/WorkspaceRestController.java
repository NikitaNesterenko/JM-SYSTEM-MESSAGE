package jm.controller;

import jm.Workspace;
import jm.WorkspaceService;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspace")
public class WorkspaceRestController {

    private WorkspaceService workspaceService;

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<Workspace>> allWorkspaces() {
        List<Workspace> workspaces = null;
        workspaces = workspaceService.gelAllWorkspaces();
        return new ResponseEntity<>(workspaces, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteWorkspace(@RequestParam("id") Long id) {
        workspaceService.deleteWorkspace(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createWorkspace(@RequestBody Workspace workspace) {
        workspaceService.createWorkspace(workspace);
        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<?> updateWorkspace(@RequestBody Workspace workspace) {
        workspaceService.updateWorkspace(workspace);
        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public ResponseEntity<Workspace> getWorkspaceById(@RequestParam("id") Long id) {
        Workspace workspace = workspaceService.getWorkspaceById(id);
        return new ResponseEntity<>(workspace, HttpStatus.OK);
    }

}
