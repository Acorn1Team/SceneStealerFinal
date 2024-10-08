package pack.admin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pack.dto.ActorDto;
import pack.dto.ActorInfoDto;
import pack.dto.CharacterDto;
import pack.dto.ItemDto_a;
import pack.dto.ItemInfoDto;
import pack.dto.StyleInfoDto;
import pack.dto.ProductDto;
import pack.dto.ShowActorDto;
import pack.dto.ShowDto;
import pack.dto.StyleDto;
import pack.dto.StyleItemDto;
import pack.entity.Actor;
import pack.entity.Show;
import pack.entity.Character;
import pack.entity.Item;
import pack.entity.Product;
import pack.entity.Style;
import pack.entity.StyleItem;
import pack.repository.ActorsRepository;
import pack.repository.CharacterLikesRepository;
import pack.repository.CharactersRepository;
import pack.repository.ItemsRepository;
import pack.repository.ShowActorRepository;
import pack.repository.ShowsRepository;
import pack.repository.StylesRepository;
import pack.repository.StyleItemRepository;

@Repository
public class AdminMainModel {
		@Autowired
		ShowsRepository showsRepo;
		@Autowired
		ActorsRepository actorsRepo;
		@Autowired
		ShowActorRepository showActorRepo;
		@Autowired
		CharactersRepository charactersRepo;
		@Autowired
		StylesRepository stylesRepo;
		@Autowired
		ItemsRepository itemsRepo;
		@Autowired
		StyleItemRepository styleItemRepo;
		@Autowired
		CharacterLikesRepository characterLikeRepo;
		
		// 전체 작품 목록
		public List<ShowDto> searchShows() {
	        return showsRepo.findAll().stream().map(Show::toDto).toList();
	    }
		
		// 작품명 자동완성
		public List<ShowDto> searchShows(String term) {
	        return showsRepo.findByTitleContaining(term).stream().map(this::toShowDto_a).toList();
	    }
		
		
		// 작품 추가
		public int insertShow(ShowDto dto) {
	        Show showentity = showsRepo.save(ShowDto.toEntity(dto));
	        return showentity.getNo(); // 자동 생성된 ID를 반환
		}
		
		// 배우 이름으로 존재 여부 판단 (있으면 해당 배우 번호, 없으면 0을 반환)
		public int checkActor(String actorName) {
			Optional<Actor> actor = actorsRepo.findByName(actorName);
			return actor.isPresent() ? actor.get().getNo() : 0;
		}
		
		// 배우 추가
		public int insertActor(String actorName) {
			ActorDto actordto = new ActorDto();
			actordto.setName(actorName);
	        Actor actorentity = actorsRepo.save(ActorDto.toEntity(actordto));
	        return actorentity.getNo();
		}
		
		// 작품-배우 관계 추가
		public void insertShowActor(int showNo, int actorNo) {
			ShowActorDto dto = new ShowActorDto();
			dto.setShowNo(showNo);
			dto.setActorNo(actorNo);
			showActorRepo.save(ShowActorDto.toEntity(dto));
		}
		
		// 배역 추가
		public int insertCharacter(CharacterDto dto) {
			return charactersRepo.save(CharacterDto.toEntity(dto)).getNo();
		}
		
		// 배역 직접 추가
		public ActorInfoDto insertCharacterDIY(CharacterDto dto) {
			Character c = charactersRepo.save(CharacterDto.toEntity(dto));
			ActorInfoDto actorInfoDto = new ActorInfoDto(c.getNo(), c.getActor().getName(), c.getName(), c.getPic());
			return actorInfoDto;
		}
		
		// Show-PK로 작품 조회
		public ShowDto searchShow(int no) {
			Optional<Show> optionalShow = showsRepo.findById(no);
			if (optionalShow.isPresent()) {
			    return toShowDto_a(optionalShow.get());
			} else {
				System.out.println("Optional<Show> 가 존재하지 않습니다.");
				return null;
			}
		}
		
		// Show-PK로 배우, 배역 목록 조회
		public ArrayList<ActorInfoDto> searchActors(int no){
			ArrayList<ActorInfoDto> actorsInfo = new ArrayList<ActorInfoDto>();
			List<Character> characters = charactersRepo.findByShowNo(no);
			for(int i=0; i<characters.size(); i++) {
				ActorInfoDto actorInfo = new ActorInfoDto();
				Character character = characters.get(i);
				actorInfo.setNo(character.getNo()); // 배역번호
				actorInfo.setActor(character.getActor().getName()); // 배우명
				actorInfo.setCharacter(character.getName()); // 배역명
				actorInfo.setPic(character.getPic()); // 배역 사진
				actorsInfo.add(actorInfo);
			}
			return actorsInfo;
		}
		
		// Character-PK로 스타일 목록 조회
		public List<StyleDto> searchStyles(int no){
			return stylesRepo.findByCharacterNo(no).stream().map(Style::toDto).toList();
		}
		
		// 특정 배역의 아이템 전체 목록
		public ArrayList<ItemDto_a> searchItems(int character_no){
			ArrayList<ItemDto_a> list = new ArrayList<ItemDto_a>();
			for(StyleItem si : styleItemRepo.findByCharacterNo(character_no)) {
				ItemDto_a item = new ItemDto_a();
				item.setNo(si.getItem().getNo());
				item.setName(si.getItem().getName());
				item.setPic(si.getItem().getPic());
				item.setStyle(si.getStyle().getNo());
				item.setProduct(si.getItem().getProduct().getNo());
				list.add(item);
			}
			return list;
		}
		
		// 아이템 전체 목록
		public ArrayList<ItemDto_a> searchItems(){
			ArrayList<ItemDto_a> list = new ArrayList<ItemDto_a>();
			for(StyleItem si : styleItemRepo.findAll()) {
				ItemDto_a item = new ItemDto_a();
				item.setNo(si.getItem().getNo());
				item.setName(si.getItem().getName());
				item.setPic(si.getItem().getPic());
				item.setStyle(si.getStyle().getNo());
				item.setProduct(si.getItem().getProduct().getNo());
				list.add(item);
			}
			return list;
		}
		
		// 아이템 이름 자동완성
		public ArrayList<ItemDto_a> searchItems(String name){
			ArrayList<ItemDto_a> list = new ArrayList<ItemDto_a>();
			for(Item i: itemsRepo.findByNameContaining(name)) {
				ItemDto_a item = new ItemDto_a();
				item.setNo(i.getNo());
				item.setName(i.getName());
				item.setPic(i.getPic());
				// item.setStyle();
				item.setProduct(i.getProduct().getNo());
				list.add(item);
			}
			return list;
		}
		
		// 배역 추가
		public int insertStyle(StyleDto dto) {
			Optional<Character> character = charactersRepo.findById(dto.getCharacterNo());
			if(character.isPresent()) dto.setCharacter(Character.toDto(character.get()));
			return stylesRepo.save(StyleDto.toEntity(dto)).getNo();
		}
		
		// 아이템 추가
		public int insertItem(Item item) {
			return itemsRepo.save(item).getNo();
		}
		
		// 스타일-아이템 추가
		public void insertStyleItem(StyleItemDto dto) {
			styleItemRepo.save(StyleItemDto.toEntity(dto));
		}
		
		// 작품 관련 정보 삭제
		// 해당 작품에 등록된 모든 배역의 관련 정보를 전체 삭제 (아래 deleteCharacterInfo 메소드 사용) 후, 마지막에 작품 삭제
		@Transactional
		public void deleteShowInfo(int no) {
			Show show = showsRepo.findById(no).get();
			for(Character c : charactersRepo.findByShowNo(no)) {
				deleteCharacterInfo(c.getNo());
			}
			showsRepo.deleteById(no);
		}
				
		// 배역 관련 정보 삭제
		@Transactional
		public void deleteCharacterInfo(int no) {
			Character character = charactersRepo.findById(no).get();
			styleItemRepo.deleteByStyleIn(character.getStyles()); // Style_Item
			stylesRepo.deleteAll(character.getStyles()); // Style
			showActorRepo.deleteByShow(character.getShow()); // Show_Actor
			charactersRepo.delete(character); // Character
			characterLikeRepo.deleteByCharacter(character); // Character_Like
		}
		
		// 스타일 관련 정보 삭제
		@Transactional
		public void deleteStyleInfo(int no) {
			Style style = stylesRepo.findById(no).get();
			styleItemRepo.deleteByStyle(style); // Style_Item
			stylesRepo.delete(style); // Style
		}
		
		// 스타일-아이템 관계 삭제
		@Transactional
		public void deleteStyleItem(int styleNo, int itemNo) {
			styleItemRepo.deleteByStyleNoAndItemNo(styleNo, itemNo);
		}
		
		// 아이템 삭제 (모든 스타일-아이템 관계 삭제 후 아이템 정보 삭제)
		@Transactional
		public void deleteItem(Integer itemNo) {
			styleItemRepo.deleteByItemNo(itemNo);
			itemsRepo.deleteById(itemNo);
		}
		
		// 전체 아이템 목록
		public Page<ItemInfoDto> getItemInfos(Pageable pageable) {
	        Page<Item> items = itemsRepo.findAll(pageable);
	        return items.map(this::toItemInfoDto);
	    }
		
		
		private ShowDto toShowDto_a(Show show) {
			return new ShowDto().builder().no(show.getNo()).title(show.getTitle()).pic(show.getPic()). build();
		}
		
		private ItemInfoDto toItemInfoDto(Item item) {
		    ItemInfoDto itemInfo = new ItemInfoDto();
		    itemInfo.setNo(item.getNo());
		    itemInfo.setName(item.getName());
		    itemInfo.setPic(item.getPic());
		    
		    ProductDto product = Product.toDto(item.getProduct());
		    itemInfo.setProductNo(product.getNo());
		    itemInfo.setProductName(product.getName());
		    itemInfo.setProductPic(product.getPic());

		    List<StyleInfoDto> styleInfos = new ArrayList<>();
		    List<StyleItemDto> styleItems = styleItemRepo.findByItem(item).stream().map(StyleItem::toDto).toList();

		    for (StyleItemDto styleItem : styleItems) {
		        StyleInfoDto si = new StyleInfoDto();
		        si.setNo(styleItem.getNo());
		        Style style = stylesRepo.findById(styleItem.getStyleNo()).get();
		        si.setStyle(Style.toDto(style));
		        Character character = style.getCharacter();
		        Actor actor = character.getActor();
		        Show show = character.getShow();
		        si.setActorInfo(new ActorInfoDto(character.getNo(), actor.getName(), character.getName(), character.getPic()));
		        si.setShowTitle(show.getTitle());
		        styleInfos.add(si);
		    }
		    itemInfo.setStyleInfos(styleInfos);
		    return itemInfo;
		}

}
