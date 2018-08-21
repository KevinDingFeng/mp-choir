package com.shenghesun.service;

import com.shenghesun.dao.SongSectionDao;
import com.shenghesun.entity.SongSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class SongSectionService {

    @Autowired
    private SongSectionDao songSectionDao;


    public List<SongSection> findMySection() {
        Long userId = 1L;
        return songSectionDao.findAll(new Specification<SongSection>() {
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
    
    public SongSection save(SongSection songSection) {
    	return songSectionDao.save(songSection);
    }
    
    public List<SongSection> findByChoirId(Long id){
    	return songSectionDao.findByChoirIdOrderBySortAsc(id);
    }
    
    public void delete(List<SongSection> songSections) {
    	 songSectionDao.deleteAll(songSections);
    }
}
