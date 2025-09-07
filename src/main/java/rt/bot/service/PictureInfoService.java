package rt.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rt.bot.entity.PictureInfo;
import rt.bot.repo.PictureInfoRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PictureInfoService {

    private final PictureInfoRepository pictureInfoRepository;

    public List<String> findGoogleDriveFileIdsByPeriod(PictureInfo.Period period) {
        return pictureInfoRepository.findGoogleDriveFileIdsByPeriod(period);
    }

    public void addNewPicsToDB(Set<String> onlyInFolder, PictureInfo.Period period) {
        if (onlyInFolder == null || onlyInFolder.isEmpty()) return;
        for (String id : onlyInFolder) {
            PictureInfo pic = new PictureInfo();
            pic.setGoogleDriveFileId(id);
            pic.setPeriod(period);
            pictureInfoRepository.save(pic);
            log.info("Добавлено новое изображение с id {} для периода {}", id, period.name());
        }
    }

    public void removeAbsentPicsFromDB(Set<String> onlyInDB) {
        pictureInfoRepository.deleteAllById(onlyInDB);
    }

    public String getActualPicId(PictureInfo.Period period) {
        String result;
        List<PictureInfo> pictureInfos = pictureInfoRepository.findByLastQueriedIsNullAndPeriod(period);
        if (!pictureInfos.isEmpty()) {
            result = pictureInfos.getFirst().getGoogleDriveFileId();
        } else {
            pictureInfos = pictureInfoRepository.findByPeriodAndLastQueriedIsNotNullOrderByLastQueriedAsc(period);
            if (!pictureInfos.isEmpty()) {
                result = pictureInfos.getFirst().getGoogleDriveFileId();
            } else result = null;
        }
        updateLastQueried(result);
        return result;
    }

    private void updateLastQueried(String picId) {
        if (picId == null) return;
        Optional<PictureInfo> pic = pictureInfoRepository.findById(picId);
        if (pic.isPresent()) {
            pic.get().setLastQueried(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
            pictureInfoRepository.save(pic.get());
        } else {
            log.error("Не удалось найти картинку по id {}", picId);
        }
    }
}
