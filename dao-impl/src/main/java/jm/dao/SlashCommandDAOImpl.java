package jm.dao;

import jm.api.dao.SlashCommandDao;
import jm.model.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
@Transactional
public class SlashCommandDAOImpl extends AbstractDao<SlashCommand> implements SlashCommandDao {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandDAOImpl.class);


    @Override
    public SlashCommand getByName(String commandName) {
       try {
           return (SlashCommand) entityManager.createNativeQuery("select * from slash_commands where name=?")
                   .setParameter(1, commandName)
                   .getSingleResult();
       } catch (NoResultException e){
           return null;
       }

    }
}
