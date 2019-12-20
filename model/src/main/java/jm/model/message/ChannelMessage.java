//package jm.model.message;
//
//import jm.model.Bot;
//import jm.model.Channel;
//import jm.model.User;
//import lombok.*;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//import java.util.Set;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
//@ToString
//@Entity
//@Table(name = "channel_messages")
//public class ChannelMessage extends Message {
//    @ManyToOne
//    @JoinColumn(name = "channel_id")
//    private Channel channel;
//
//    @ManyToMany(cascade = CascadeType.REFRESH)
//    @JoinTable(
//            name = "starred_message_user",
//            joinColumns = @JoinColumn(name = "msg_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
//    private Set<User> starredByWhom;
//
//    @Column(name = "shared_message_id")
//    private Long sharedMessageId;
//
//    public ChannelMessage(Channel channel, User user, String content, LocalDateTime dateCreate) {
//        super(user, content, dateCreate);
//        this.channel = channel;
//    }
//
//    public ChannelMessage(Channel channel, Bot bot, String content, LocalDateTime dateCreate) {
//        super(bot, content, dateCreate);
//        this.channel = channel;
//    }
//
//    public ChannelMessage(Long id, Channel channel, User user, String content, LocalDateTime dateCreate) {
//        super(id, user, content, dateCreate);
//        this.channel = channel;
//    }
//
//    //two constructors for sharing messages
//    public ChannelMessage(Channel channel, User user, String content, LocalDateTime dateCreate, Long sharedMessageId) {
//        super(user, content, dateCreate);
//        this.channel = channel;
//        this.sharedMessageId = sharedMessageId;
//    }
//
//    public ChannelMessage(Channel channel, Bot bot, String content, LocalDateTime dateCreate, Long sharedMessageId) {
//        super(bot, content, dateCreate);
//        this.channel = channel;
//        this.sharedMessageId = sharedMessageId;
//    }
//}
