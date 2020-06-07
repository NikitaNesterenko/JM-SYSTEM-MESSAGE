package jm;

import jm.dto.SlashCommandDto;
import org.codehaus.jackson.JsonProcessingException;

public interface TrelloSlashCommand {
    String getCommand(SlashCommandDto command) throws com.fasterxml.jackson.core.JsonProcessingException;
}
