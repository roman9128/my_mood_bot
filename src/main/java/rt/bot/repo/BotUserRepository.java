package rt.bot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rt.bot.entity.BotUser;

import java.util.List;

@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    @Query("SELECT bu.telegramUserId FROM BotUser bu")
    List<Long> findAllTelegramUserIds();
}