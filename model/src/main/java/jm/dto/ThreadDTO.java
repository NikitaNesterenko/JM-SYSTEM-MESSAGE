package jm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreadDTO {
    private Long id;
    private MessageDTO message;

    public ThreadDTO(MessageDTO message) {
        this.message = message;
    }
}
