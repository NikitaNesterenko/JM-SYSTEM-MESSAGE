package jm.controller;

import jm.PluginService;
import jm.dto.ZoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/callback")
public class PluginCallbackController {

    private PluginService<ZoomDTO> zoomPlugin;

    @Autowired
    public void setPluginService(PluginService<ZoomDTO> pluginService) {
        this.zoomPlugin = pluginService;
    }

    @GetMapping("/zoom")
    public String zoomCallback(@RequestParam("code") String code, Principal principal) throws IOException {
        zoomPlugin.setToken(code, principal.getName());
        return "jm-message-config";
    }

}
