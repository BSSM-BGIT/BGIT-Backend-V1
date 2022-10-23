package bssm.db.bssmgit.domain.user.service;

import bssm.db.bssmgit.domain.user.domain.ImaginaryNumber;
import bssm.db.bssmgit.domain.user.domain.User;
import bssm.db.bssmgit.domain.user.facade.ImaginaryNumberFacade;
import bssm.db.bssmgit.domain.user.facade.UserFacade;
import bssm.db.bssmgit.global.exception.CustomException;
import bssm.db.bssmgit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ImaginaryNumberService {

    private final UserFacade userFacade;
    private final ImaginaryNumberFacade imaginaryNumberFacade;

    @Scheduled(cron = "0 * * * 3 ?") // 매주 수요일 허수 관련 정보 초기화
    public void init() {
        userFacade.findAll()
                        .forEach(user ->
                                {
                                    user.initImaginary();
                                    user.initVotingCount();
                                });
        imaginaryNumberFacade.removeAll();
    }

    // TODO : top 3가 중복된 값이면 어떻게 처리할것인가
    @Scheduled(cron = "0 * * * 4 ?") // 매주 목요일 투표 탑 3 허수 지정
    public void designationImaginaryNumber() {
        List<ImaginaryNumber> imaginaryNumbers = imaginaryNumberFacade.findUsersByTop3();
        ArrayList<Long> userIds = new ArrayList<>();

        for (ImaginaryNumber imaginaryNumber : imaginaryNumbers) {
            userIds.add(imaginaryNumber.getUserId());
        }

        ArrayList<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            User user = userFacade.findById(userId);
            user.updateImaginary();
            users.add(user);
        }

        userFacade.saveAll(users);
    }

    // TODO: 리팩터링 필요
    @Transactional // 만약에 수요일이면 허수 투표 가능
    public void votingImaginaryNumber(Long userId) { // WEDNESDAY
        if (LocalDate.now().getDayOfWeek().name().equals("SUNDAY")) {
            User user = userFacade.getCurrentUser();
            if (user.getVotingCount() == 0) {
                throw new CustomException(ErrorCode.NOT_ENOUGH_VOTING_COUNT);
            }

            user.reductionVotingCount();
            userFacade.save(user);

            Optional<ImaginaryNumber> optionalImaginaryNumber = imaginaryNumberFacade.findAll()
                    .stream()
                    .filter(imaginary -> Objects.equals(imaginary.getUserId(), userId))
                    .findFirst();

            if (optionalImaginaryNumber.isPresent()) {
                optionalImaginaryNumber.get().increaseVoting();
            } else {
                ImaginaryNumber imaginaryNumber = imaginaryNumberFacade.save(new ImaginaryNumber(userId));
                imaginaryNumber.initVotingNumber();
                imaginaryNumber.increaseVoting();
            }
        } else {
            throw new CustomException(ErrorCode.NOT_SUPPORTED_DAY);
        }
    }
}
