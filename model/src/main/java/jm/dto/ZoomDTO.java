package jm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoomDTO {
    private Integer id;
    private String topic;
    private String type;
    private String created_at;
    private String join_url;
    private String token;
    private String redirectUri;

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", type='" + type + '\'' +
                ", created_at='" + created_at + '\'' +
                ", join_url='" + join_url + '\'' +
                '}';
    }
}