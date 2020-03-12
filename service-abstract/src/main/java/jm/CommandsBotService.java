package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.dto.SlashCommandDto;

public interface CommandsBotService {

    String getWsCommand(SlashCommandDto command) throws JsonProcessingException;

    String sendMsg(SlashCommandDto command) throws JsonProcessingException;
}
