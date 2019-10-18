package jm.api.dao;

import jm.model.InviteToken;

public interface InviteTokenDAO {

    void persist(InviteToken inviteToken);

    void deleteById(Long id);

}
