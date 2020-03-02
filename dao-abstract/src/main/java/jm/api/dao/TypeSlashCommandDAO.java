package jm.api.dao;

import jm.model.TypeSlashCommand;

import java.util.List;

public interface TypeSlashCommandDAO {

    List<TypeSlashCommand> getAll();

    void persist(TypeSlashCommand type);

    void deleteById(Long id);

    TypeSlashCommand merge(TypeSlashCommand type);

    TypeSlashCommand getById(Long id);

    TypeSlashCommand getByName(String name);
}
