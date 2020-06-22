package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.dto.SlashCommandDto;

public interface GoogleDriveSlashCommand {
    String getCommand(SlashCommandDto command) throws JsonProcessingException;
}
