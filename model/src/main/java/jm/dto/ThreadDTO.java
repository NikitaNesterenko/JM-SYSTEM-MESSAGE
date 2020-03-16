package jm.dto;

import jm.model.Message;
import jm.model.ThreadChannel;
import jm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ThreadDTO {
    private Long id;
    private MessageDTO message;

    public ThreadDTO(MessageDTO message) {
        this.message = message;
    }

    public ThreadDTO(@NonNull Long id, @NonNull MessageDTO message) {
        this.id = id;
        this.message = message;
    }

    public ThreadDTO(@NonNull ThreadChannel threadChannel) {
        this.id = threadChannel.getId();
        this.message = new MessageDTO(threadChannel.getMessage());
    }
}
