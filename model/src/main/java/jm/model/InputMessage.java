package jm.model;

/* Сервис будет принимать содержащие inputMassage STOMP сообщения, тела которых представляют собой JSON объекты.*/

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InputMessage {

    private Long id;  // нужно для редактирования сообщений

    private Channel channel;  // нужно чтобы новые и измененные сообщения не попадали сначала в любой открытый ченнел

    private String inputMassage;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;

    private User user;

    private Bot bot;
}
