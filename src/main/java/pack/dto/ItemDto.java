package pack.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pack.entity.Item;
import pack.entity.Product;
import pack.entity.Style;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
	  private Integer no;
	  private String pic;  // URL or file path
	  private String name;
	  private String path;
	  private StyleDto style;
	  private ProductDto product;
	  private Integer productNo;
	  
	  public static Item toEntity(ItemDto dto) {
			return Item.builder()
					.no(dto.getNo())
	    			.pic(dto.getPic())
	    			.name(dto.getName())
	    			.product(ProductDto.toEntity(dto.getProduct()))
	    			.path(dto.getPath())
	    			.build();
		}

}
