package pack.login;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import pack.dto.UserDto;
import pack.entity.Coupon;
import pack.entity.CouponUser;
import pack.entity.User;
import pack.repository.CouponUserRepository;
import pack.repository.UsersRepository;

@RestController
@RequestMapping("/api/naver")
public class NaverController {

	@Value("${naver.client-id}")
	private String clientId;

	@Value("${naver.client-secret}")
	private String clientSecret;

	@Value("${naver.redirect-uri}")
	private String redirectUri;

	@Value("${naver.api-url.token}")
	private String naverTokenUrl;

	@Value("${naver.api-url.user-info}")
	private String naverUserInfoUrl;

	@Autowired
	private UsersRepository urps;
	
	@Autowired
	private CouponUserRepository curps;

	@PostMapping("/token")
	public ResponseEntity<Map<String, Object>> getNaverToken(@RequestBody Map<String, String> payload) {
		String code = payload.get("code");
		String state = payload.get("state");

		try {
			// 네이버로부터 액세스 토큰 요청
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(naverTokenUrl + "?grant_type=authorization_code&client_id=" + clientId
							+ "&client_secret=" + clientSecret + "&code=" + code + "&state=" + state))
					.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
					.POST(HttpRequest.BodyPublishers.noBody()).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				String content = response.body();
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> tokenInfo = mapper.readValue(content, Map.class);
				return ResponseEntity.ok(tokenInfo);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping
	public ResponseEntity<Map<String, Object>> handleNaverLogin(@RequestBody Map<String, String> payload) {
		String accessToken = payload.get("accessToken");

		try {
			// 액세스 토큰으로 사용자 정보 요청
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(naverUserInfoUrl))
					.header("Authorization", "Bearer " + accessToken).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				String content = response.body();
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> userInfo = mapper.readValue(content, Map.class);
				System.out.println("User Info from Naver: " + userInfo);

				Map<String, Object> userInfoResponse = (Map<String, Object>) userInfo.get("response");
				String naverId = String.valueOf(userInfoResponse.get("id"));
				String nickname = String.valueOf(userInfoResponse.get("nickname"));
				String profile_image = String.valueOf(userInfoResponse.get("profile_image"));

				String name = String.valueOf(userInfoResponse.get("name"));
				String email = String.valueOf(userInfoResponse.get("email"));
				String mobile = String.valueOf(userInfoResponse.get("mobile"));

				Optional<User> userEmailCheck = urps.findByEmail(email);
				Optional<User> userOptional = urps.findByIdN(naverId);
				User user;
				
				Map<String, Object> result;
				
				if (userEmailCheck.isPresent()) {
					user = userEmailCheck.get();
					if (user.getIdN() == null) {
						user.setIdN(naverId);
						urps.save(user);
					}
					System.out.println("naver : " + user.getNo());
					result = Map.of("status", "login", "user", user.getNo());
				} else if (userOptional.isPresent()) {
					user = userOptional.get();
					result = Map.of("status", "login", "user", user.getNo());
				} else {
					String finalNick = nickname + naverId;
					user = User.builder()
						.idN(naverId)
						.email(email)
						.nickname(finalNick.length() > 10 ? finalNick.substring(0, 9) : finalNick)
						.name(name)
						.tel(mobile)
						.pic(profile_image)
						.build();
					User userResult = urps.save(user);
					
					CouponUser cu = CouponUser.builder()
												.user(User.builder().no(userResult.getNo()).build())
												.coupon(Coupon.builder().no(1).build())
												.build();
					curps.save(cu);
					
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

	@PostMapping("/delete-token")
	public ResponseEntity<String> deleteNaverToken(@RequestBody Map<String, String> payload) {
		String accessToken = payload.get("accessToken");

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=" + clientId
							+ "&client_secret=" + clientSecret + "&access_token=" + accessToken
							+ "&service_provider=NAVER"))
					.POST(HttpRequest.BodyPublishers.noBody()).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return ResponseEntity.ok("Token deleted successfully");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to delete token");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting token");
		}
	}
	
	@PostMapping("/user-info")
	public ResponseEntity<Map<String, Object>> getNaverUserInfo(@RequestBody Map<String, String> payload) {
	    String accessToken = payload.get("accessToken");

	    try {
	        // 액세스 토큰으로 사용자 정보 요청
	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(naverUserInfoUrl))
	                .header("Authorization", "Bearer " + accessToken)
	                .GET()
	                .build();

	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	        if (response.statusCode() == 200) {
	            String content = response.body();
	            ObjectMapper mapper = new ObjectMapper();
	            Map<String, Object> userInfo = mapper.readValue(content, Map.class);
	            System.out.println("User Info from Naver: " + userInfo);

	            Map<String, Object> userInfoResponse = (Map<String, Object>) userInfo.get("response");
	            String id = (String) userInfoResponse.get("id");

	            boolean exists = false;
	            Integer userNo = null;

	            // 네이버 ID가 DB에 있는지 확인
	            Optional<User> userOptional = urps.findByIdN(id);
	            if (userOptional.isPresent()) {
	                User user = userOptional.get();
	                userNo = user.getNo(); // DB에서 해당 유저의 userNo를 가져옴
	                exists = true;
	            }

	            // 결과 생성 및 반환
	            Map<String, Object> result = new HashMap<>();
	            result.put("result", exists);
	            result.put("userNo", userNo); // userNo 추가

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
