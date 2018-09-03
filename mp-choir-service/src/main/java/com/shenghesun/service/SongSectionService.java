package com.shenghesun.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shenghesun.dao.SongSectionDao;
import com.shenghesun.entity.Choir;
import com.shenghesun.entity.SongSection;
import com.shenghesun.entity.SongSection.SectionStatusEnum;
import com.shenghesun.util.FileIOUtil;

@Service
public class SongSectionService {

//    @Autowired
//    private UserService userService;

    @Value("${upload.file.path}")
    private String audioFilePath;

    @Autowired
    private SongSectionDao songSectionDao;

    public SongSection findById(Long sectionId){
        if(sectionId == null || sectionId <1){
            return null;
        }
        return songSectionDao.findById(sectionId).orElse(null);
    }


    public List<SongSection> findMySection(Long userId) {
        if(userId == null || userId<1){
            return null;
        }
        return songSectionDao.findAll(new Specification<SongSection>() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 427377379732718701L;

			@Override
            public Predicate toPredicate(Root<SongSection> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("userId"), userId));
                Predicate and = cb.and(cb.equal(root.get("status").as(SongSection.SectionStatusEnum.class),
                        SongSection.SectionStatusEnum.RECORDED),
                        cb.equal(root.get("choir").get("status"), 0));
                predicate.getExpressions().add(cb.or(and, cb.equal(root.get("status")
                        .as(SongSection.SectionStatusEnum.class), SongSection.SectionStatusEnum.NO_RECORDING)));
                return predicate;
            }
        });
    }

    public List<Choir> findMyWritting(Long userId){
        if(userId == null || userId<1){
            return null;
        }
        List<SongSection> list = songSectionDao.findAll(new Specification<SongSection>() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 7019607968375983706L;

			@Override
            public Predicate toPredicate(Root<SongSection> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                predicate.getExpressions().add(cb.equal(root.get("userId"), userId));
                predicate.getExpressions().add(cb.equal(root.get("choir").get("status"), 1));
                return predicate;
            }
        });
        return list.stream().map(SongSection::getChoir).collect(Collectors.toList());
    }

    public SongSection save(SongSection songSection) {
        return songSectionDao.save(songSection);
    }

    public List<SongSection> findByChoirId(Long id) {
        return songSectionDao.findByChoirIdOrderBySortAsc(id);
    }

    public void delete(List<SongSection> songSections) {
        songSectionDao.deleteAll(songSections);
    }

    public boolean uploadAudioFile(Long sectionId, MultipartFile audioFile) throws IOException {
        String path = FileIOUtil.uploadFile(audioFile.getOriginalFilename(), audioFile.getInputStream(),
                audioFilePath, false);
        SongSection songSection = songSectionDao.findById(sectionId).orElse(null);
        if (songSection == null) {
            return false;
        }
        songSection.setAudioPath(path);
        songSection.setStatus(SectionStatusEnum.RECORDED);
        songSectionDao.save(songSection);
        return true;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

}
