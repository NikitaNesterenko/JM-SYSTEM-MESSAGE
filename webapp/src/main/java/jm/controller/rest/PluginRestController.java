package jm.controller.rest;

import jm.PluginService;
import jm.dto.ZoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/rest/plugin")
public class PluginRestController {

  private PluginService<ZoomDTO> zoomPlugin;

  @Autowired
  public void setPluginService(PluginService<ZoomDTO> pluginService) {
    this.zoomPlugin = pluginService;
  }

  @GetMapping("/zoom")
  public ResponseEntity<ZoomDTO> zoomOAuth(Principal principal) {
    if (principal != null) {
      return ResponseEntity.ok(zoomPlugin.create(principal.getName()));
    }
    return ResponseEntity.badRequest().build();
  }
}
