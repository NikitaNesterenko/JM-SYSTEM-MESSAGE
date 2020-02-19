package jm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jm.model.SlashCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlashCommandDto {
        private Long id;
        private String name;
        private String url;
        private String description;
        private String hints;
        private String command;
        private Long channelId;
        private Long userId;
        private Long botId;

        public SlashCommandDto(SlashCommand sc){
                this.id = sc.getId();
                this.name = sc.getName();
                this.url = sc.getUrl();
                this.description = sc.getDescription();
                this.hints = sc.getHints();
        }

}
