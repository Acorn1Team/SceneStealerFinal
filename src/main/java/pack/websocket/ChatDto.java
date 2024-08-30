package pack.websocket;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {

	private Integer no;

	private boolean closeChat;

	private boolean sendCheck;

	private String content;
	
	private boolean sendAdmin;
	
	private int chatNo;

	private Date date;
	
	private int userNo;
	
	private int receiverNo;

	public static Chat toEntity(ChatDto dto) {
		return Chat.builder()
				.no(dto.getNo())
				.closeChat(dto.isCloseChat())
				.content(dto.getContent())
				.sendAdmin(dto.isSendAdmin())
				.date(dto.getDate())
				.build();
	}
}
