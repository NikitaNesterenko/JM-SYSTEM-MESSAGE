package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.dto.SlashCommandDto;
import jm.model.Workspace;

public interface CommandsBotService {

    String getWsCommand(SlashCommandDto command, Workspace workspace) throws JsonProcessingException;

    String sendMsg(SlashCommandDto command) throws JsonProcessingException;
}
