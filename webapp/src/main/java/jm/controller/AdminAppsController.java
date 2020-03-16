package jm.controller;

import jm.BotService;
import jm.SlashCommandService;
import jm.TypeSlashCommandService;
import jm.WorkspaceService;
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

    private final BotService botService;
    private final WorkspaceService workspaceService;
    private final SlashCommandService slashCommandService;
    private final TypeSlashCommandService typeSlashCommandService;

    public AdminAppsController(BotService botService, WorkspaceService workspaceService, SlashCommandService slashCommandService, TypeSlashCommandService typeSlashCommandService) {
        this.botService = botService;
        this.workspaceService = workspaceService;
        this.slashCommandService = slashCommandService;
        this.typeSlashCommandService = typeSlashCommandService;
    }

    @GetMapping(value = "/bots")
    public ModelAndView addCustomBot(Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("workspaces", workspaceService.getWorkspaceListByLogin(userName));
        model.addAttribute("bots", botService.gelAllBots());
        return new ModelAndView("apps/create-custom-bot");
    }

    @GetMapping(value = "/bots/{botId}")
    public ModelAndView editCustomBot(@PathVariable Long botId, Model model) {
        model.addAttribute("bot", botService.getBotById(botId));
        model.addAttribute("types", typeSlashCommandService.getAllTypesSlashCommands());
        model.addAttribute("commands", slashCommandService.getSlashCommandsByBotId(botId));
        return new ModelAndView("apps/edit-custom-bot");
    }
}
