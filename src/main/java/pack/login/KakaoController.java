package pack.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import pack.dto.UserDto;
import pack.entity.User;
import pack.repository.UsersRepository;

import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/kakao")
public class KakaoController {

	@Value("${kakao.api-url.user-info}")
	private String kakaoUserInfoUrl;

	@Autowired
	private UsersRepository urps;

	@PostMapping
	public ResponseEntity<Map<String, Object>> handleKakaoLogin(@RequestBody Map<String, String> payload) {
		String accessToken = payload.get("accessToken");

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(kakaoUserInfoUrl))
					.header("Authorization", "Bearer " + accessToken).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == HttpURLConnection.HTTP_OK) {
				String content = response.body();

				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> userInfo = mapper.readValue(content, Map.class);

				String kakaoId = String.valueOf(userInfo.get("id"));

				Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
				String email = (String) kakaoAccount.get("email");

				Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

				String nickname = profile != null ? (String) profile.get("nickname") : null;
				String profilePic = profile != null ? (String) profile.get("thumbnail_image_url") : null;

				System.out.println("\n\n\n" + kakaoId + " " + email + " " + nickname + " " + profilePic + "\n\n\n");

				// 사용자 정보 저장 또는 업데이트
				Optional<User> userEmailCheck = urps.findByEmail(email);
				Optional<User> userOptional = urps.findByIdK(kakaoId);
				User user;
				System.out.println(kakaoId);

				Map<String, Object> result;
				
				if(userEmailCheck.isPresent()) {
					user = userEmailCheck.get();
					if (user.getIdK() == null) {
						user.setIdK(kakaoId);
						urps.save(user);
					}
					result = Map.of("status", "login", "user", user.getNo());
				} else if (userOptional.isPresent()) {
					user = userOptional.get();
					result = Map.of("status", "login", "user", user.getNo());
				} else {
					user = User.builder()
							.id(kakaoId)
							.email(email)
							.nickname(nickname)
							.pic(profilePic)
							.build();
					urps.save(user);
					result = Map.of("status", "signup", "user", user.getNo());
				}
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
