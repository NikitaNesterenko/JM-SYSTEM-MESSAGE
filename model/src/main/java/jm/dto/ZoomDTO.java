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
    private String createdAt;
    private String joinUrl;
    private String token;
    private String redirectUri;

    private static class Builder {
        private ZoomDTO zoomDTO;

        public Builder () {
            zoomDTO = new ZoomDTO();
        }

        public Builder setId(Integer id) {
            zoomDTO.id = id;
            return this;
        }

        public Builder setTopic(String topic) {
            zoomDTO.topic = topic;
            return this;
        }

        public Builder setType(String type) {
            zoomDTO.type = type;
            return this;
        }

        public Builder setCreatedAt(String createdAt) {
            zoomDTO.createdAt = createdAt;
            return this;
        }

        public Builder setJoinUrl(String joinUrl) {
            zoomDTO.joinUrl = joinUrl;
            return this;
        }

        public Builder setToken(String token) {
            zoomDTO.token = token;
            return this;
        }

        public Builder setRedirectUri(String redirectUri) {
            zoomDTO.redirectUri = redirectUri;
            return this;
        }

        public ZoomDTO build() {
            return zoomDTO;
        }
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", type='" + type + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", joinUrl='" + joinUrl + '\'' +
                '}';
    }
}
