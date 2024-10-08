package pack.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pack.dto.CharacterDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;

    private String name;
    private Integer likesCount = 0;

    @Column(name = "pic")
    private String pic;

    @ManyToOne
    @JoinColumn(name = "actor_no")
    private Actor actor;
    
    @OneToMany(mappedBy = "character")
    @Builder.Default
    private List<Style> styles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "show_no")
    private Show show;

    @OneToMany(mappedBy = "character")
    @Builder.Default
    private List<CharacterLike> characterLikes = new ArrayList<>();

    public static CharacterDto toDto(Character entity) {
        return CharacterDto.builder()
                .no(entity.getNo())
                .name(entity.getName())
                .likesCount(entity.getLikesCount() != null ? entity.getLikesCount() : 0) // null 체크
                .pic(entity.getPic())
                .actorNo(entity.getActor().getNo())
                .showNo(entity.getShow().getNo())
                .characterLikeNo(entity.getCharacterLikes().stream().map(characterLike -> characterLike.getUser().getNo()).collect(Collectors.toList()))
                .styleNo(entity.getStyles().stream().map(Style::getNo).collect(Collectors.toList()))
//                .actor(Actor.toDto(entity.getActor()))
//                .show(Show.toDto(entity.getShow()))
//                .characterLikes(entity.getCharacterLikes().stream().map(CharacterLike::toDto).collect(Collectors.toList()))
//                .styles(entity.getStyles().stream().map(Style::toDto).collect(Collectors.toList()))
                .build();
    }
}
