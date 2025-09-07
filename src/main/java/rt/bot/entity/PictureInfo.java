package rt.bot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "picture_infos")
public class PictureInfo {
    @Id
    private String googleDriveFileId;
    @Enumerated(EnumType.STRING)
    private Period period;
    private LocalDateTime lastQueried;

    public enum Period {
        MORNING, NOON, EVENING
    }
}