package pack.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pack.dto.UserDto;
import pack.model.UserModel;

public class CustomUserDetails implements UserDetails {

	private String id;
	private Integer no;
	private String pwd;
	
    @Autowired
    private UserModel userModel;
	
    public CustomUserDetails(UserDto userDto) {
    	
    }
    
    public CustomUserDetails(String id, Integer no, String password) {
        this.id = id;
        this.no = no;
        this.pwd = password;
    }
    
    public UserDetails loadUserByNo(Integer no) throws UsernameNotFoundException {
        // 사용자 번호를 기반으로 사용자 정보를 조회
        UserDto user = userModel.getUserByNo(no);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with no: " + no);
        }
        return new CustomUserDetails(user);
    }

    public String getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한이 필요하다면 이 부분을 수정
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
