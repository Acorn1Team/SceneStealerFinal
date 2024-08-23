package pack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pack.entity.Post;

public interface PostsRepository extends JpaRepository<Post, Integer>{
	
    List<Post> findAll();
    
	public List<Post> findTop3ByOrderByNoDesc();
	
	public List<Post> findByUserNo(int no);
	
	public int deleteByNo(int no);
	
}
