package pack.entity;


import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pack.dto.AlertDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private User user;

    private String category;
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "date")
    private java.util.Date date;

    private Boolean isRead;
    
    private String path;
    
    public static AlertDto toDto (Alert entity) {
    	return AlertDto.builder()
    			.no(entity.getNo())
    			.userNo(entity.getUser().getNo())
//    			.user(User.toDto(entity.getUser()))
    			.category(entity.getCategory())
    			.content(entity.getContent())
    			.date(entity.getDate())
    			.isRead(entity.getIsRead())
    			.path(entity.getPath())
    			.build();
    }
}

