package jm.analytic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChannelActivity {

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate date;

    private Integer privateChannels = 0;
    private Integer publicChannels = 0;
    private Integer directMessages = 0;

    private Integer messagesInPublicChannels = 0;
    private Integer messagesInPrivateChannels = 0;
    private Integer messagesInDirectMessages = 0;
}
