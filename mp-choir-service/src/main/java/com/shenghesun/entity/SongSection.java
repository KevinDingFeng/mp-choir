package com.shenghesun.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 歌曲分段
 *
 * @ClassName: SongSection
 * @Description: TODO
 * @author: yangzp
 * @date: 2018年8月13日 上午11:34:02
 */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
public class SongSection extends BaseEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7496767747592566932L;

//    /**
//     * 合唱团id
//     */
//    @Column(insertable = false, updatable = false)
//    private Long choirId;

    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "choir_id")
    private Choir choir;

    /**
     * 用户id
     */
    @Column
    private Long userId;
    
    /**
     * 单曲的TSID
     */
    @Column
    private String tsID;

    /**
     * /切割后的资源 id，通过此 id 使用短音频资源
     */
    @Column
    private String resourceId;
    
    /**
     * 分段后的位置
     */
    @Column
    private int sort;
    
    /**
     * 短音频链接
     */
    @Transient
    private String path;

    private SectionStatusEnum status = SectionStatusEnum.NO_CLAIM;

    public enum SectionStatusEnum {
        NO_CLAIM("未认领"), NO_RECORDING("未录制"), RECORDED("录制结束");

        private String text;

        SectionStatusEnum(String text) {
            this.text = text;
        }

		public String getText() {
			return text;
		}
    }

}
