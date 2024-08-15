package pack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pack.entity.Character;
import pack.entity.Show;

public interface ShowsRepository extends JpaRepository<Show, Integer>{

	@Query("SELECT s FROM Show AS s JOIN Character AS c ON s.no = c.show.no GROUP BY s.title ORDER BY SUM(c.likesCount) DESC")
	public List<Show> findShowAll();
	
	
//	@Query("SELECT c FROM Character c JOIN c.show s JOIN c.styles st JOIN st.items i WHERE s.no = :showNo")
//	public List<Character> findSubData(int showNo);
}
