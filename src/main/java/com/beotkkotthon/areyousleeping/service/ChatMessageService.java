package com.beotkkotthon.areyousleeping.service;

import com.beotkkotthon.areyousleeping.domain.User;
import com.beotkkotthon.areyousleeping.domain.nosql.ChatMessage;
import com.beotkkotthon.areyousleeping.domain.nosql.ChatMessageList;
import com.beotkkotthon.areyousleeping.dto.request.ChatMessageDto;
import com.beotkkotthon.areyousleeping.dto.response.ChatMessageListDto;
import com.beotkkotthon.areyousleeping.dto.response.ChatMessageResponseDto;
import com.beotkkotthon.areyousleeping.exception.CommonException;
import com.beotkkotthon.areyousleeping.exception.ErrorCode;
import com.beotkkotthon.areyousleeping.repository.ChatMessageListRepository;
import com.beotkkotthon.areyousleeping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageListRepository chatMessageListRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendChatMessage(String teamId, ChatMessageDto requestDto) {

        User user = userRepository.findByNickname(requestDto.sender());

        OffsetDateTime parsedDate = OffsetDateTime.parse(requestDto.sendTime());

        Optional<ChatMessageList> chatMessageList = chatMessageListRepository.findById(teamId);
        if(chatMessageList.isPresent()){
            ChatMessageList chat = chatMessageList.get();
            chat.getMessages().add(
                    ChatMessage.builder()
                            .type(requestDto.type())
                            .sender(user)
                            .content(requestDto.content())
                            .date(parsedDate)
                            .build()
            );
            chatMessageListRepository.save(chat);

        } else throw new CommonException(ErrorCode.NOT_FOUND_TEAM);

        ChatMessageResponseDto responseDto = ChatMessageResponseDto.builder()
                .type(requestDto.type())
                .senderNickname(user.getNickname())
                .senderProfileImage(user.getProfileImageUrl())
                .content(requestDto.content())
                .sendTime(requestDto.sendTime())
                .build();
        messagingTemplate.convertAndSend("/subscribe/team/" + teamId, responseDto);
    }

    public ChatMessageListDto getChatMessages(String teamId, int page, int size) {
        ChatMessageList chatMessageList = chatMessageListRepository.findById(teamId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_TEAM));

        List<ChatMessage> sortedMessages = chatMessageList.getMessages()
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getDate).reversed())
                .collect(Collectors.toList());

        // 실제 페이지 사이즈보다 하나 더 많은 데이터를 불러오기 위해 size + 1 사용 -> hasNext 계산을 위함
        int start = page * size;
        int end = Math.min((page + 1) * size + 1, sortedMessages.size()); // end 계산 시 +1

        List<ChatMessage> paginatedMessages = sortedMessages.subList(start, Math.min(end, sortedMessages.size()));

        boolean hasNext = paginatedMessages.size() > size;

        List<ChatMessage> finalMessages = hasNext ? paginatedMessages.subList(0, size) : paginatedMessages;

        List<ChatMessageResponseDto> messageDtos = convertToMessageDTOList(finalMessages);

        return ChatMessageListDto.builder()
                .messageList(messageDtos)
                .hasNext(hasNext)
                .build();
    }


    private List<ChatMessage> sortMessagesByDate(ChatMessageList chat) {
        return chat.getMessages()
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getDate))
                .collect(Collectors.toList());
    }

    private List<ChatMessageResponseDto> convertToMessageDTOList(List<ChatMessage> messages) {
        return messages.stream()
                .map(msg -> ChatMessageResponseDto.builder()
                        .type(msg.getType())
                        .senderNickname(userRepository.findById(msg.getSender().getId())
                                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER)).getNickname())
                        .senderProfileImage(userRepository.findById(msg.getSender().getId())
                                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER)).getProfileImageUrl())
                        .content(msg.getContent())
                        .sendTime(msg.getDate().toString())
                        .build())
                .collect(Collectors.toList());
    }
}