package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
import com.miniproject.eventastic.mail.service.MailService;
import com.miniproject.eventastic.mail.service.entity.dto.MailTemplate;
import com.miniproject.eventastic.organizerWallet.entity.OrganizerWallet;
import com.miniproject.eventastic.organizerWallet.service.OrganizerWalletService;
import com.miniproject.eventastic.pointsTrx.entity.PointsTrx;
import com.miniproject.eventastic.pointsTrx.service.PointsTrxService;
import com.miniproject.eventastic.pointsWallet.entity.PointsWallet;
import com.miniproject.eventastic.pointsWallet.service.PointsWalletService;
import com.miniproject.eventastic.referralCodeUsage.entity.ReferralCodeUsage;
import com.miniproject.eventastic.referralCodeUsage.entity.composite.ReferralCodeUsageId;
import com.miniproject.eventastic.referralCodeUsage.service.ReferralCodeUsageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.entity.dto.register.RegisterResponseDto;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersRegisterService;
import com.miniproject.eventastic.users.service.UsersService;
import com.miniproject.eventastic.voucher.entity.Voucher;
import com.miniproject.eventastic.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersRegisterServiceImpl implements UsersRegisterService {

  private final static int POINTS_REWARD = 10000;

  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;
  private final PointsWalletService pointsWalletService;
  private final OrganizerWalletService organizerWalletService;
  private final UsersService usersService;
  private final PointsTrxService pointsTrxService;
  private final VoucherService voucherService;
  private final ReferralCodeUsageService referralCodeUsageService;
  private final MailService mailService;

  @Override
  @Transactional
  public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
    // * validate
    String username = registerRequestDto.getUsername();
    String email = registerRequestDto.getEmail();
    validateCredentials(username, email);
    log.info("Registering user: " + username);
    log.info("Testing credentials");

    // * init new user
    Users newUser = createUser(registerRequestDto);
    log.info("New user ID: " + newUser.getId());

    // * init points wallet
    PointsWallet newPointsWallet = initPointsWallet(newUser);
    log.info("New points wallet for: " + newPointsWallet.getUser().getUsername());

    // * if Organizer init organizer wallet
    if (newUser.getIsOrganizer()) {
      OrganizerWallet newOrganizerWallet = initOrganizerWallet(newUser);
    }

    // * Generate and assign referral code
    String ownedReferralCode = generateReferralCode();
    newUser.setOwnedRefCode(ownedReferralCode);
    usersRepository.save(newUser);

    // * process referral code and update in points trx if using
    Voucher referralVoucher = null;
    if (newUser.getRefCodeUsed() != null && !newUser.getRefCodeUsed().isEmpty()) {
      String refCodeUsed = newUser.getRefCodeUsed();
      referralVoucher = useRefCode(newUser, refCodeUsed);
    }

    // * send email
    sendWelcomeEmail(newUser);
    return new RegisterResponseDto(newUser, referralVoucher);


  }

  public void validateCredentials(String username, String email) {
    if (usersRepository.findByUsername(username).isPresent() || usersRepository.findByEmail(email).isPresent()) {
      throw new DuplicateCredentialsException("Username or email already exists");
    }
  }

  public Users createUser(RegisterRequestDto requestDto) {
//    Users newUser = modelMapper.map(requestDto, Users.class);
    Users newUser = new Users();
    newUser.setUsername(requestDto.getUsername());
    newUser.setEmail(requestDto.getEmail());
    newUser.setFullName(requestDto.getFullName());
    newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    newUser.setRefCodeUsed(requestDto.getRefCodeUsed());
    newUser.setIsOrganizer(requestDto.getIsOrganizer());
    usersRepository.save(newUser);
    log.info("Registered user: {}", newUser);
    return newUser;
  }

  public PointsWallet initPointsWallet(Users user) {
    PointsWallet pointsWallet = new PointsWallet();
    pointsWallet.setUser(user);
    pointsWallet.setPoints(0);
    pointsWalletService.savePointsWallet(pointsWallet);
    user.setPointsWallet(pointsWallet);
    return pointsWallet;
  }

  public OrganizerWallet initOrganizerWallet(Users user) {
    OrganizerWallet organizerWallet = new OrganizerWallet();
    organizerWallet.setOrganizer(user);
    organizerWallet.setBalance(BigDecimal.ZERO);
    organizerWalletService.saveWallet(organizerWallet);
    user.setOrganizerWallet(organizerWallet);
    return organizerWallet;
  }

  public String generateReferralCode() {
    String ownedReferralCode;
    do {
      ownedReferralCode = UUID.randomUUID().toString().substring(0, 7).toUpperCase();
    } while (checkRefCodeExist(ownedReferralCode));
    return ownedReferralCode;
  }

  public boolean checkRefCodeExist(String code) {
    return usersService.getUserByOwnedCode(code) != null;
  }

  public Voucher useRefCode(Users user, String refCodeUsed) throws RuntimeException {
    Voucher newVoucher = new Voucher();
    Users owner = usersService.getUserByOwnedCode(refCodeUsed);
    if (owner != null) {
      // ** Add points to the code owner's wallet

      PointsWallet ownerPointsWallet = pointsWalletService.getPointsWallet(owner);

      ownerPointsWallet.setPoints(ownerPointsWallet.getPoints() + POINTS_REWARD);
      ownerPointsWallet.setAwardedAt(Instant.now());
      ownerPointsWallet.setExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));

      // update in points trx
      PointsTrx pointsTrx = new PointsTrx();
      pointsTrx.setPointsWallet(ownerPointsWallet);
      pointsTrx.setPoints(POINTS_REWARD);
      pointsTrx.setDescription("Referral code usage by new user");

      // ** Give voucher to user of the code
      // time set up
      ZonedDateTime endOfDay = ZonedDateTime.now().with(LocalTime.MAX);
      Instant expiresAt = endOfDay.toInstant().plus(90, ChronoUnit.DAYS);
      String code = generateVoucher();

      // init voucher
      newVoucher.setCode(code);
      newVoucher.setDescription("Thank you for using " + owner.getUsername() + "'s referral code!");
      newVoucher.setPercentDiscount(10);
      newVoucher.setCreatedAt(Instant.now());
      newVoucher.setExpiresAt(expiresAt);
      newVoucher.setAwardee(user);
      newVoucher.setUseLimit(1);
      newVoucher.setIsActive(true);

      // ** Log the usage of referral code
      ReferralCodeUsage usage = new ReferralCodeUsage(
          new ReferralCodeUsageId(user.getId(), owner.getId()),
          user,
          owner,
          Instant.now()
      );

      // make sure all the saves are executed, if not catch it
      try {
        pointsWalletService.savePointsWallet(ownerPointsWallet);
        pointsTrxService.savePointsTrx(pointsTrx);
        voucherService.saveVoucher(newVoucher);
        referralCodeUsageService.saveReferralCodeUsage(usage);
      } catch (Exception e) {
        log.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        throw new RuntimeException("Referral code usage have failed!");
      }

      log.info("Referral code from user {} was used by new user {}", owner.getUsername(), user.getUsername());
      return newVoucher;
    } else {
      log.info("No referral code was used by new user {}", user.getUsername());
      throw new UserNotFoundException("No code found, make sure you've entered the right referral code");
    }
  }

  public String generateVoucher() {
    String code;
    // check if code exists and regenerate code until it isn't in database
    do {
      code = "REF10" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    } while (checkVoucher(code));
    return code;
  }

  private boolean checkVoucher(String code) {
    try {
      voucherService.getVoucher(code);
    } catch (VoucherNotFoundException e) {
      log.info("Intended to check voucher code {}", code);
      return false;
    }
    return true;
  }

  public void sendWelcomeEmail(Users user) {
    String fullName = user.getFullName();
    String email = user.getEmail();
    MailTemplate welcomeMail = new MailTemplate();
    // ! TODO : uncomment in production, suspend email sending for local
      mailService.sendEmail(welcomeMail.buildWelcomeTemp(email, user.getFullName()));
  }

}
