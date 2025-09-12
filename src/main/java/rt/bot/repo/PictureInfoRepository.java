package rt.bot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rt.bot.entity.PictureInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface PictureInfoRepository extends JpaRepository<PictureInfo, String> {

    @Query("SELECT pi FROM PictureInfo pi WHERE pi.period = :period ORDER BY pi.lastQueried DESC LIMIT 1")
    Optional<PictureInfo> findTopByPeriodOrderByLastQueriedDesc(@Param("period") PictureInfo.Period period);

    @Query("SELECT p.googleDriveFileId FROM PictureInfo p WHERE p.period = :period")
    List<String> findGoogleDriveFileIdsByPeriod(@Param("period") PictureInfo.Period period);

    @Query("SELECT p FROM PictureInfo p WHERE p.lastQueried IS NULL AND p.period = :period")
    List<PictureInfo> findByLastQueriedIsNullAndPeriod(@Param("period") PictureInfo.Period period);

    @Query("SELECT p FROM PictureInfo p WHERE p.period = :period AND p.lastQueried IS NOT NULL ORDER BY p.lastQueried ASC")
    List<PictureInfo> findByPeriodAndLastQueriedIsNotNullOrderByLastQueriedAsc(@Param("period") PictureInfo.Period period);
}
