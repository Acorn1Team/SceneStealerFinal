package pack.entity;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;
import pack.dto.UserDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;

    private String id;
    private String pwd;
    private String name;
    private String tel;
    private String email;
    private String zipcode;
    private String address;
    private Integer reward;
    private String nickname;
    private String bio;

    @Column(name = "pic")
    private String pic;  // URL or file path

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Posts> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Comments> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Reviews> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Coupon> coupons = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Alerts> alerts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<CharacterLikes> characterLikes = new ArrayList<>();
    
    public static UserDto toDto (User entity) {
    	return UserDto.builder()
    			.id(entity.getId())
    			.pwd(entity.getPwd())
    			.name(entity.getName())
    			.tel(entity.getTel())
    			.email(entity.getEmail())
    			.zipcode(entity.getZipcode())
    			.address(entity.getAddress())
    			.reward(entity.getReward())
    			.nickname(entity.getNickname())
    			.bio(entity.getBio())
    			.pic(entity.getPic())
    			.posts(entity.getPosts())
    			.comments(entity.getComments())
    			.reviews(entity.getReviews())
    			.coupons(entity.getCoupons())
    			.alerts(entity.getAlerts())
    			.characterLikes(entity.getCharacterLikes())
    			.build();
    }
}

