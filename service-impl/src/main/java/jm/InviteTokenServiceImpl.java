package jm;

import jm.api.dao.InviteTokenDAO;
import jm.model.InviteToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class InviteTokenServiceImpl implements InviteTokenService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private InviteTokenDAO inviteTokenDAO;

    @Autowired
    public InviteTokenDAO setInviteTokenDAO(InviteTokenDAO inviteTokenDAO) { return this.inviteTokenDAO = inviteTokenDAO; }

    public InviteToken getById(Long id) {
        return inviteTokenDAO.getById(id);
    }

    @Override
    public void createInviteToken(InviteToken inviteToken) {
        inviteTokenDAO.persist(inviteToken);
    }

    @Override
    public void deleteInviteToken(Long id) {
        inviteTokenDAO.deleteById(id);
    }

    @Override
    public Optional<InviteToken> getByHash(String hash) { return inviteTokenDAO.getByHash(hash); }
}
