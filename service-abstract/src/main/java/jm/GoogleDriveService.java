package jm;

import jm.dto.FileItemGoogleDriveDTO;
import jm.dto.WorkspaceDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleDriveService {

    String authorize(WorkspaceDTO workspace, String principalName) throws GeneralSecurityException, IOException;

    void firstStartClientAuthorization(String token, WorkspaceDTO workspace, String principalName);

    String addFolder(String commandBody, String token);

    String uploadFile(String token);

    List<FileItemGoogleDriveDTO> getFileItemGoogleDriveDTOS();

    String makePublic(String fileId);

    String deleteFile(String fileId);
}
