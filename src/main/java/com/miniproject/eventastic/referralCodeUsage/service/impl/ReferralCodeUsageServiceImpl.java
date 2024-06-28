package com.miniproject.eventastic.referralCodeUsage.service.impl;

import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.repository.ReferralCodeUsageRepository;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@Data
@RequiredArgsConstructor
public class ReferralCodeUsageServiceImpl implements ReferralCodeUsageService {

  private final ReferralCodeUsageRepository referralCodeUsageRepository;

  @Override
  public void save(ReferralCodeUsage usage) {
    referralCodeUsageRepository.save(usage);
  }

  @Override
  public List<ReferralCodeUsage> findAll() {
    return referralCodeUsageRepository.findAll();
  }
}
