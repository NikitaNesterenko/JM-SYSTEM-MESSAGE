package jm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ZoomDTO {
    private Long id;
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

        public Builder setId(Long id) {
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
