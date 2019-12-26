package jm.api.dao;

import jm.model.InviteToken;

import java.util.Optional;

public interface InviteTokenDAO {

    void persist(InviteToken inviteToken);

    InviteToken getById(Long id);

    void deleteById(Long id);

    Optional<InviteToken> getByHash(String hash);
}
