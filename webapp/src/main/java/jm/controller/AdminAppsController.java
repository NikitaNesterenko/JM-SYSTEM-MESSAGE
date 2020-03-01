package jm.controller;

import jm.BotService;
import jm.SlashCommandService;
import jm.UserService;
import jm.WorkspaceService;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/apps")
public class AdminAppsController {

    private BotService botService;
    private WorkspaceService workspaceService;
    private UserService userService;
    private SlashCommandService slashCommandService;

    @Autowired
    public AdminAppsController(BotService botService, WorkspaceService workspaceService, UserService userService,
                               SlashCommandService slashCommandService) {
        this.botService = botService;
        this.workspaceService = workspaceService;
        this.userService = userService;
        this.slashCommandService = slashCommandService;
    }

    @GetMapping(value = "/bots")
    public ModelAndView addCustomBot(Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByLogin(userName);
        model.addAttribute("workspaces", workspaceService.getWorkspacesByUserId(user.getId()));
        model.addAttribute("bots", botService.gelAllBots());
        return new ModelAndView("apps/create-custom-bot");
    }

    @GetMapping(value = "/bots/{botId}")
    public ModelAndView editCustomBot(@PathVariable Long botId, Model model) {
        model.addAttribute("bot", botService.getBotById(botId));
        model.addAttribute("commands", slashCommandService.getSlashCommandsByBotId(botId));
        return new ModelAndView("apps/edit-custom-bot");
    }
}
