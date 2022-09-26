package bssm.db.bssmgit.domain.user.web.dto.response;

import bssm.db.bssmgit.domain.user.domain.User;
import lombok.Data;

@Data
public class BojResponseDto {

    private final String bojId;
    private final long solvedCount;
    private final long exp;
    private final long tier;
    private final long maxStreak;
    private final String bojImg;
    private final UserResponseDto user;

    public BojResponseDto(User user) {
        this.bojId = user.getBojId();
        this.solvedCount = user.getSolvedCount();
        this.exp = user.getExp();
        this.tier = user.getTier();
        this.maxStreak = user.getMaxStreak();
        this.bojImg = user.getBojImg();
        this.user = new UserResponseDto(user);
    }
}