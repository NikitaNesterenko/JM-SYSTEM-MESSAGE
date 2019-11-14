package jm.controller.rest;

import jm.WorkspaceService;
import jm.model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/api/workspaces")
public class WorkspaceRestController {

    private WorkspaceService workspaceService;

    @Autowired
    public void setWorkspaceService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(workspaceService.getWorkspaceById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity createWorkspace(@RequestBody Workspace workspace) {
        try {
            workspaceService.createWorkspace(workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateChannel(@RequestBody Workspace workspace) {
        try {
            workspaceService.updateWorkspace(workspace);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteWorkspace(@PathVariable("id") Long id) {
        workspaceService.deleteWorkspace(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Workspace>> getAllWorkspaces() {
        return new ResponseEntity<>(workspaceService.gelAllWorkspaces(),HttpStatus.OK);
    }

}
