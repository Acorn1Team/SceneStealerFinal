package pack.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pack.entity.Item;
import pack.entity.Style;
import pack.entity.StyleItem;

public interface StyleItemRepository extends JpaRepository<StyleItem, Integer> {
	List<StyleItem> findByStyleNo(int no);
	
	List<StyleItem> findByItem(Item item);
	
	// 해당 배역의 전체 아이템 목록
	@Query("SELECT si FROM StyleItem si WHERE si.style.no IN (SELECT s.no FROM Character c JOIN c.styles s WHERE c.no = :no)")
	List<StyleItem> findByCharacterNo(@Param("no") int no);
	
	List<StyleItem> findByStyleNoIn(List<Integer> list);
	
	void deleteByStyleIn(List<Style> list); // 배역의 전체 스타일에 있는 아이템 연결 관계 삭제
	
	void deleteByStyle(Style style); // 특정 스타일의 전체 스타일-아이템 관계 삭제
	
	void deleteByItemNo(Integer no); // 특정 아이템의 전체 스타일-아이템 관계 삭제
	
	void deleteByStyleNoAndItemNo(Integer styleNo, Integer itemNo); // 스타일-아이템 관계 삭제
	
	@Query("SELECT COUNT(DISTINCT style.no) FROM StyleItem")
	int findAllExceptNoItem();
	
	@Query("SELECT DISTINCT style FROM StyleItem")
	Page<Style> findAllStyleExceptNoItem(Pageable pageable);
}
