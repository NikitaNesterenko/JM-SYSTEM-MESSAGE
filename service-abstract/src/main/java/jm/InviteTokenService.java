package jm;

import jm.model.InviteToken;

public interface InviteTokenService {

    void createInviteToken(InviteToken inviteToken);

    InviteToken getById(Long id);

    void deleteInviteToken(Long id);

    InviteToken getByHash(String hash);
}
