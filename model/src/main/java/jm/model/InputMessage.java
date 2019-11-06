package jm.model;

/* Сервис будет принимать содержащие inputMassage STOMP сообщения, тела которых представляют собой JSON объекты.*/

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

public class InputMessage {
    //TODO
    private String inputMassage;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime dateCreate;
    private User user;

    public InputMessage() {
    }

    public InputMessage(String inputMassage, LocalDateTime dateCreate, User user) {
        this.inputMassage = inputMassage;
        this.dateCreate = dateCreate;
        this.user = user;
    }

    public String getInputMassage() {
        return inputMassage;
    }

    public void setInputMassage(String inputMassage) {
        this.inputMassage = inputMassage;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "InputMessage{" +
                "inputMassage='" + inputMassage + '\'' +
                ", dateCreate=" + dateCreate +
                ", user=" + user +
                '}';
    }
}
