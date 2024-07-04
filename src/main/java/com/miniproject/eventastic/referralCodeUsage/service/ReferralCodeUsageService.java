package com.miniproject.eventastic.referralCodeUsage.service;

import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUsersDto;
import com.miniproject.eventastic.referralCodeUsage.entity.dto.ReferralCodeUseCountDto;
import com.miniproject.eventastic.users.entity.Users;
import java.util.List;

public interface ReferralCodeUsageService {

  void saveReferralCodeUsage(ReferralCodeUsage usage);

  ReferralCodeUseCountDto getReferralCodeUseCount(Users codeOwner);

  List<ReferralCodeUsersDto> getReferralCodeUsers(Users codeOwner);
}
