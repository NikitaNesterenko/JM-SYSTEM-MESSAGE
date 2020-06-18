package jm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileItemGoogleDriveDTO {

    private String name;

    private String id;

    private String thumbnailLink;

}