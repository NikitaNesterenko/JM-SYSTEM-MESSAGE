package jm.api.dao;

import jm.model.SlashCommand;

import java.util.List;

public interface SlashCommandDao {

    List<SlashCommand> getAll();

    void persist(SlashCommand command);

    void deleteById(Long id);

    SlashCommand merge(SlashCommand command);

    SlashCommand getById(Long id);

    SlashCommand getByName(String commandName);



}
