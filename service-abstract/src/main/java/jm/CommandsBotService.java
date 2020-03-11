package jm;

import com.fasterxml.jackson.core.JsonProcessingException;
import jm.dto.SlashCommandDTO;

public interface CommandsBotService {

    String getWsCommand(SlashCommandDTO command) throws JsonProcessingException;

    String sendMsg(SlashCommandDTO command) throws JsonProcessingException;
}
