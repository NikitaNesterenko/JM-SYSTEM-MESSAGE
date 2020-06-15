package jm;

import jm.dto.WorkspaceDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleDriveService {

    String authorize(WorkspaceDTO workspace, String principalName) throws GeneralSecurityException, IOException;

    void firstStartClientAuthorization(String token, WorkspaceDTO workspace, String principalName);
}
