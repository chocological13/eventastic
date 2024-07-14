package com.miniproject.eventastic.exceptions;

import com.miniproject.eventastic.exceptions.event.CategoryNotFoundException;
import com.miniproject.eventastic.exceptions.event.DuplicateEventException;
import com.miniproject.eventastic.exceptions.event.EventEndedException;
import com.miniproject.eventastic.exceptions.event.EventNotFoundException;
import com.miniproject.eventastic.exceptions.event.ReviewNotFoundException;
import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.exceptions.trx.InsufficientPointsException;
import com.miniproject.eventastic.exceptions.trx.NotAwardeeException;
import com.miniproject.eventastic.exceptions.trx.OrganizerWalletNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PaymentMethodNotFoundException;
import com.miniproject.eventastic.exceptions.trx.PointsTrxNotFoundException;
import com.miniproject.eventastic.exceptions.trx.SeatUnavailableException;
import com.miniproject.eventastic.exceptions.trx.TicketNotFoundException;
import com.miniproject.eventastic.exceptions.trx.TicketTypeNotFoundException;
import com.miniproject.eventastic.exceptions.trx.VoucherInvalidException;
import com.miniproject.eventastic.exceptions.trx.VoucherNotFoundException;
import com.miniproject.eventastic.exceptions.user.AttendeeNotFoundException;
import com.miniproject.eventastic.exceptions.user.DuplicateCredentialsException;
import com.miniproject.eventastic.exceptions.user.ReferralCodeUnusedException;
import com.miniproject.eventastic.exceptions.user.UserNotFoundException;
import com.miniproject.eventastic.responses.Response;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
   private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = new HashMap<>();

   static {
      EXCEPTION_STATUS_MAP.put(CategoryNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(DuplicateEventException.class, HttpStatus.CONFLICT);
      EXCEPTION_STATUS_MAP.put(EventEndedException.class, HttpStatus.GONE);
      EXCEPTION_STATUS_MAP.put(EventNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(ReviewNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(ImageNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(InsufficientPointsException.class, HttpStatus.BAD_REQUEST);
      EXCEPTION_STATUS_MAP.put(NotAwardeeException.class, HttpStatus.BAD_REQUEST);
      EXCEPTION_STATUS_MAP.put(OrganizerWalletNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(PaymentMethodNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(PointsTrxNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(SeatUnavailableException.class, HttpStatus.CONFLICT);
      EXCEPTION_STATUS_MAP.put(TicketNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(TicketTypeNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(VoucherInvalidException.class, HttpStatus.BAD_REQUEST);
      EXCEPTION_STATUS_MAP.put(VoucherNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(AttendeeNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(DuplicateCredentialsException.class, HttpStatus.BAD_REQUEST);
      EXCEPTION_STATUS_MAP.put(ReferralCodeUnusedException.class, HttpStatus.BAD_REQUEST);
      EXCEPTION_STATUS_MAP.put(UserNotFoundException.class, HttpStatus.NOT_FOUND);
      EXCEPTION_STATUS_MAP.put(AccessDeniedException.class, HttpStatus.FORBIDDEN);
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<Response<String>> handleException(Exception ex) {
      HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
      String message = ex.getClass().getSimpleName() + ": " + ex.getMessage() + "\n" + status.getReasonPhrase();
      return Response.failedResponse(status.value(), message, null);
   }
}
