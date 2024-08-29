package pack.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pack.config.CustomUserDetails;
import pack.dto.UserDto;
import pack.repository.UsersRepository;

import java.util.Collections;

@Service
public class AuthModel implements UserDetailsService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 로그인
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        pack.entity.User user = userRepository.findByLoginId(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // 사용자 no 값을 추가하여 CustomUserDetails 객체를 반환
        return new CustomUserDetails(
                user.getId(),
                user.getNo(), // user.getNo()는 사용자 no를 반환하는 메소드
                user.getPwd()
        );
    }

    // 회원가입
    public void saveUser(pack.entity.User user) {
        // 비밀번호를 암호화합니다.
        String encodedPassword = passwordEncoder.encode(user.getPwd());

        // 암호화된 비밀번호를 User 엔티티에 설정
        user.setPwd(encodedPassword);

        // 사용자 정보를 저장합니다.
        userRepository.save(user);
    }

    public void saveUserFromDto(UserDto userDto) {
        pack.entity.User user = UserDto.toEntity(userDto);
        saveUser(user);
    }
}