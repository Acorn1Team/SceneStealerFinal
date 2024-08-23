// 메인(작품, 배역, 배우, 스타일, 아이템) CRUD
package pack.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pack.admin.model.AdminMainModel;
import pack.admin.scrap.Scrap;
import pack.dto.ActorInfoDto;
import pack.dto.CharacterDto;
import pack.dto.FashionRequest;
import pack.dto.ShowDto;
import pack.dto.ShowInfoDto;
import pack.dto.StyleDto;

@RestController
public class AdminMainController {
	@Autowired
	private Scrap scrap;
	
	@Autowired
	private AdminMainModel dao;
	
	// 자동완성 - 입력값이 없을 때) 등록된 작품 전체
	@GetMapping("/admin/show/autocomplete")
    public List<ShowDto> autocompleteall() {
        return dao.searchShows();
    }
	
	// 자동완성 - 입력값이 있을 때) 등록된 작품 중 일부
    @GetMapping("/admin/show/autocomplete/{term}")
    public List<ShowDto> autocomplete(@PathVariable("term") String term) {
        return dao.searchShows(term);
    }

	// 웹스크래핑) 검색한 작품 정보 조회
	@GetMapping("/admin/scrap/show/{keyword}")
	public ShowDto ScrapShow(@PathVariable("keyword") String keyword) {
		return scrap.scrapShow(keyword);
	}
	
	// 웹스크래핑)  검색한 작품의 배우, 배역 정보 조회
	@GetMapping("/admin/scrap/actors/{keyword}")
	public ArrayList<ActorInfoDto> ScrapActors(@PathVariable("keyword") String keyword) {
		return scrap.scrapActors(keyword);
	}
	
	// 작품, 배우, 배역 INSERT) 4개 테이블 처리: Show, Actor, ShowActor, Character
	@PostMapping("/admin/fashion")
	public int insertShowActorsCharacters(@RequestBody FashionRequest fashionRequest) {
		List<ActorInfoDto> actors = fashionRequest.getActors();
	    ShowDto show = fashionRequest.getShow();
	    
	    // 작품 추가
	    int show_no = dao.insertShow(show); // 작품 추가 후 PK 반환
	    
	    // 배우, 배역 추가
	    for(ActorInfoDto actorandcharacter : actors) {
	    	int actor_no =  dao.insertActor(actorandcharacter.getActor()); // 배우 추가 후 PK 반환
	    	dao.insertShowActor(show_no, actor_no); // 작품-배우 관계 추가
	    	
	    	CharacterDto characterDto = new CharacterDto();
	    	characterDto.setShowNo(show_no);
	    	characterDto.setActorNo(actor_no);
	    	characterDto.setName(actorandcharacter.getCharacter());
	    	characterDto.setPic(actorandcharacter.getPic());
	    	dao.insertCharacter(characterDto);
	    }
	    return show_no;
	}
	
	// 작품의 배우, 배역 목록 조회
	@GetMapping("/admin/fashion/show/{no}")
	public ShowInfoDto getShowInfo(@PathVariable("no") int no) { // 작품 PK
		ShowInfoDto showInfo = new ShowInfoDto(); // 작품 정보와 배우, 배역 목록 정보를 담는 객체
		showInfo.setShow(dao.searchShow(no));
		showInfo.setActorsInfo(dao.searchActors(no));
		return showInfo;
	}

	@GetMapping("/admin/fashion/character/{no}") // 캐릭터의 
	public List<StyleDto> getStylesInfo(@PathVariable("no") int no) { // 캐릭터 PK
		return dao.searchStyles(no);
	}
}
