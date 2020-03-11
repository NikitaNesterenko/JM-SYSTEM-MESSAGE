package jm;

import jm.api.dao.TypeSlashCommandDAO;
import jm.model.TypeSlashCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TypeSlashCommandServiceImpl implements TypeSlashCommandService {

    @Autowired
    private TypeSlashCommandDAO typeSlashCommandDAO;

    @Override
    public void createTypeSlashCommand(TypeSlashCommand type) {
        typeSlashCommandDAO.persist(type);
    }

    @Override
    public void deleteTypeSlashCommand(Long id) {
        typeSlashCommandDAO.deleteById(id);
    }

    @Override
    public List<TypeSlashCommand> getAllTypesSlashCommands() {
        return typeSlashCommandDAO.getAll();
    }

    @Override
    public TypeSlashCommand getTypeSlashCommandById(Long id) {
        return typeSlashCommandDAO.getById(id);
    }

    @Override
    public TypeSlashCommand getTypeSlashCommandByName(String name) {
        return typeSlashCommandDAO.getByName(name);
    }

    @Override
    public void updateTypeSlashCommand(TypeSlashCommand type) {
        typeSlashCommandDAO.merge(type);
    }
}
