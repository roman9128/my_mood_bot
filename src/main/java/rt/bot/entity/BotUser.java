package rt.bot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bot_users")
public class BotUser {
    @Id
    private Long telegramUserId;
    private String telegramUsername;
    private String telegramFirstName;
    private String telegramLastName;
    @Enumerated(EnumType.STRING)
    private Status status;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Status {
        GUEST, USER, ADMIN
    }
}