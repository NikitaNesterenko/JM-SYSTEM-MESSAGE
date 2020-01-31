package jm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlashCommandDto {
        private String command;
        private Long channel_id;
        private Long user_id;


}
