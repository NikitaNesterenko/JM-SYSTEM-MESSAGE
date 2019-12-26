package jm;

import jm.model.InviteToken;

import java.util.Optional;

public interface InviteTokenService {

    void createInviteToken(InviteToken inviteToken);

    InviteToken getById(Long id);

    void deleteInviteToken(Long id);

    Optional<InviteToken> getByHash(String hash);
}
