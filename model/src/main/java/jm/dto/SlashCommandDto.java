package jm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jm.model.Bot;
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
    private Long typeId;

    public SlashCommandDto(SlashCommand sc) {
        if (sc != null) {
            this.id = sc.getId();
            this.name = sc.getName();
            this.url = sc.getUrl();
            this.description = sc.getDescription();
            this.hints = sc.getHints();
            this.typeId = sc.getType().getId();

            Bot bot = sc.getBot();
            if (bot != null) {
                this.botId = bot.getId();
            }
        }
    }

    public void setId(Number id) {
        this.id = id.longValue();
    }

    public void setBotId(Number botId) {
        this.botId = botId.longValue();
    }

    public void setTypeId(Number typeId) {
        this.typeId = typeId.longValue();
    }

    private static class Builder {
        private SlashCommandDto slashCommandDto;

        public Builder() {
            slashCommandDto = new SlashCommandDto();
        }

        public Builder setId(Long id) {
            slashCommandDto.id = id;
            return this;
        }

        public Builder setName(String name) {
            slashCommandDto.name = name;
            return this;
        }

        public Builder setUrl(String url) {
            slashCommandDto.url = url;
            return this;
        }

        public Builder setDescription(String description) {
            slashCommandDto.description = description;
            return this;
        }

        public Builder setHints(String hints) {
            slashCommandDto.hints = hints;
            return this;
        }

        public Builder setCommand(String command) {
            slashCommandDto.command = command;
            return this;
        }

        public Builder setUserId(Long userId) {
            slashCommandDto.userId = userId;
            return this;
        }

        public Builder setBotId(Long botId) {
            slashCommandDto.botId = botId;
            return this;
        }

        public Builder setTypeId(Long typeId) {
            slashCommandDto.typeId = typeId;
            return this;
        }

        public SlashCommandDto build() {
            return slashCommandDto;
        }
    }

}
