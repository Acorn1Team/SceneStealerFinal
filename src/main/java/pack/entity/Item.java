package pack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pack.dto.ItemDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;

    @Column(name = "pic")
    private String pic;  // URL or file path

    @ManyToOne
    @JoinColumn(name = "style_no")
    private Style style;

    @ManyToOne
    @JoinColumn(name = "product_no")
    private Product product;
    
    public static ItemDto toDto(Item entity) {
    	return ItemDto.builder()
    			.no(entity.getNo())
    			.pic(entity.getPic())
    			.style(entity.getStyle())
    			.product(entity.getProduct())
    			.build();
    }
}
