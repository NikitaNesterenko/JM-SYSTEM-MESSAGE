package jm.model;

import java.time.LocalDateTime;

public class KafkaMessage {
    private Long channelId;
    private Long userId;
    private String content;


    @Override
    public String toString() {
        return "KafkaMessage{" +
                "channelId=" + channelId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                '}';
    }

    public KafkaMessage() {
    }

    public KafkaMessage(Long channelId, Long userId, String content, LocalDateTime dateCreate) {
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}