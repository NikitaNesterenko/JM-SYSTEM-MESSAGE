package jm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/workspace/**")
public class WorkspaceController {
    @PostMapping(value = "/create")
    public ModelAndView addUser(@RequestParam("name") String name, @RequestParam("usersList") String[] usersList,
                                @RequestParam("owner") String owner, @RequestParam(value = "isPrivate", required = false) boolean isPrivate) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/workspace");
        return modelAndView;
    }

    @GetMapping(value = "/")
    public ModelAndView workspacePage() {
        return new ModelAndView("workspace-page");
    }
}
