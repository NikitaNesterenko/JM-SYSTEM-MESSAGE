package jm.dto;

import jm.BotService;
import jm.TypeSlashCommandService;
import jm.model.Bot;
import jm.model.SlashCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlashCommandDtoServiceImpl implements SlashCommandDtoService{
    private final BotService botService;
    private final TypeSlashCommandService typeSlashCommandService;

    @Autowired
    public SlashCommandDtoServiceImpl(BotService botService, TypeSlashCommandService typeSlashCommandService) {
        this.botService = botService;
        this.typeSlashCommandService = typeSlashCommandService;
    }

    @Override
    public SlashCommandDto toDto(SlashCommand slashCommand) {
        if (slashCommand == null) {
            return null;
        }
        SlashCommandDto dto = new SlashCommandDto(slashCommand);

        Bot bot = slashCommand.getBot();

        if (bot != null) {
            dto.setBotId(bot.getId());
        }
         return dto;
    }

    @Override
    public SlashCommand toEntity(SlashCommandDto slashCommandDto) {
        if (slashCommandDto == null){
            return null;
        }

        SlashCommand sc = new SlashCommand(slashCommandDto);
        sc.setType(typeSlashCommandService.getTypeSlashCommandById(slashCommandDto.getTypeId()));
        Bot bot = botService.getBotById(slashCommandDto.getBotId());

        if (bot != null){
            sc.setBot(bot);
        }

        return sc;
    }
}
