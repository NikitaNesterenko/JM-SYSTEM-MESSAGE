//package jm.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//import javax.persistence.*;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class VoiceMessage {
//
//    @Id
//    @Column(nullable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @EqualsAndHashCode.Include
//    private Long id;
//
//    @Column(unique = true)
//    private String name;
//
//    @Column(length = 100000)
//    private Byte[] value;
//}
