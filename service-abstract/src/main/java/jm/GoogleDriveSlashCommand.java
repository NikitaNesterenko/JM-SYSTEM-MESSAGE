package jm;

import jm.dto.SlashCommandDto;

public interface GoogleDriveSlashCommand {
    String getCommand(SlashCommandDto command) throws com.fasterxml.jackson.core.JsonProcessingException;
}
