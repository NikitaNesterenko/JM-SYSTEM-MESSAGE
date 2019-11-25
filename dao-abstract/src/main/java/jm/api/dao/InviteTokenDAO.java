package jm.api.dao;

import jm.model.InviteToken;

public interface InviteTokenDAO {

    void persist(InviteToken inviteToken);

    InviteToken getById(Long id);

    InviteToken getByHash(String hash);

    void deleteById(Long id);

}
