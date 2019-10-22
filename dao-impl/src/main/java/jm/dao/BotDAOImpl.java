package jm.dao;

import jm.api.dao.BotDAO;
import jm.model.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class BotDAOImpl extends AbstractDao<Bot> implements BotDAO {
    private static final Logger logger = LoggerFactory.getLogger(BotDAOImpl.class);

}
