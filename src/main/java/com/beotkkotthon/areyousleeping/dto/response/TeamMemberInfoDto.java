package com.beotkkotthon.areyousleeping.dto.response;

import com.beotkkotthon.areyousleeping.domain.Achievement;
import com.beotkkotthon.areyousleeping.domain.User;
import com.beotkkotthon.areyousleeping.domain.UserTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description="밤샘 메이트 조회 시 응답 dto")
public class TeamMemberInfoDto {

    private String nickname;
    private String title;
    private Boolean isActive;
    private Boolean isLeader;

    @Builder
    public TeamMemberInfoDto(UserTeam userTeam, User user, Achievement achievement) {

        this.nickname = user.getNickname();
        this.title = achievement.getTitle();
        this.isActive = userTeam.getIsActive();
        this.isLeader = userTeam.getIsLeader();

    }

}
