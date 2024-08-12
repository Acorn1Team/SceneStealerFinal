package pack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pack.dto.NoticeDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notices")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;

    private String category;
    private String title;
    private String contents;

    @Column(name = "date")
    private java.util.Date date;
    
    public static NoticeDto toDto(Notice entity) {
    	return NoticeDto.builder()
    			.no(entity.getNo())
    			.category(entity.getCategory())
    			.title(entity.getTitle())
    			.contents(entity.getContents())
    			.date(entity.getDate())
    			.build();
    }
}
