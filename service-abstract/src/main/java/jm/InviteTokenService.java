package jm;

import jm.model.InviteToken;

public interface InviteTokenService {

    void createInviteToken(InviteToken inviteToken);

    void deleteInviteToken(Long id);

}
