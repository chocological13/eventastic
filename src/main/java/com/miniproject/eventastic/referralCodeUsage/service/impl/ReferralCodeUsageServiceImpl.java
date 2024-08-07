package com.miniproject.eventastic.referralCodeUsage.service.impl;

import com.miniproject.eventastic.exceptions.user.ReferralCodeUnusedException;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUseCountDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsersDto;
import com.miniproject.eventastic.referralCodeUsage.repository.ReferralCodeUsageRepository;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralCodeUsageServiceImpl implements ReferralCodeUsageService {

  private final ReferralCodeUsageRepository referralCodeUsageRepository;

  @Override
  public void saveReferralCodeUsage(ReferralCodeUsage usage) {
    referralCodeUsageRepository.save(usage);
  }

  @Override
  public ReferralCodeUseCountDto getReferralCodeUseCount(Users codeOwner) throws ReferralCodeUnusedException {
    ReferralCodeUseCountDto dto = referralCodeUsageRepository.countReferralCodeUsageWhereOwnerIs(codeOwner);
    if (dto == null) {
      throw new ReferralCodeUnusedException("No referral code usage found");
    }
    return dto;
  }

  @Override
  public List<ReferralCodeUsersDto> getReferralCodeUsers(Users codeOwner) throws ReferralCodeUnusedException {
    List<ReferralCodeUsersDto> dto = referralCodeUsageRepository.findReferralCodeUsersWhereOwnerIs(codeOwner);
    if (dto == null) {
      throw new ReferralCodeUnusedException("No referral code usage found");
    }
    return dto;
  }


}
