package pack.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pack.dto.StyleDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="styles")
public class Style {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;

    @Column(name = "pic")
    private String pic;  // URL or file path

    @ManyToOne
    @JoinColumn(name = "character_no")
    private Character character;

    @OneToMany(mappedBy = "style")
    @Builder.Default
    private List<Item> items = new ArrayList<>();
    
    public static StyleDto toDto(Style entity) {
    	return StyleDto.builder()
    			.no(entity.getNo())
    			.pic(entity.getPic())
    			.character(entity.getCharacter())
    			.items(entity.getItems())
    			.build();
    }
}
