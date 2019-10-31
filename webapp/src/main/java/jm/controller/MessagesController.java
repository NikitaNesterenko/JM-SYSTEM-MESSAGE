package jm.controller;

import jm.Content.MessageContent;
import jm.model.InputMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;


/* В подходе Spring при работе с STOMP сообщениями, STOMP сообщения могут быть перенаправлены к @Controller классам.
 * @MessageMapping аннотация гарантирует, что если сообщение отправляется на /message, то будет вызван messagesСreation() метод.
 * Созданное представление сообщения рассылается всем подписчикам на /topic/messages, как это определено в аннотации @SendTo.*/

@Controller
public class MessagesController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public MessageContent messagesСreation(InputMessage message) throws Exception {
        return new MessageContent("<b>" + HtmlUtils.htmlEscape(message.getUser().getLogin()) + "</b>       " + HtmlUtils.htmlEscape(message.getDateCreate().toString().replace("T", "   ")) +
                "<br><br><tr><td>" + HtmlUtils.htmlEscape(message.getInputMassage()) + "<br><br></td></tr>");
    }

}
