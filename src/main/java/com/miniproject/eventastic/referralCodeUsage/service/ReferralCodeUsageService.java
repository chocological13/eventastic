package com.miniproject.eventastic.referralCodeUsage.service;

import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import java.util.List;

public interface ReferralCodeUsageService {

  void save(ReferralCodeUsage usage);

  List<ReferralCodeUsage> findAll();
}
