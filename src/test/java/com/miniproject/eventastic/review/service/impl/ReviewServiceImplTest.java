//package com.miniproject.eventastic.review.service.impl;
//
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//
//import com.miniproject.eventastic.attendee.entity.Attendee;
//import com.miniproject.eventastic.attendee.entity.AttendeeId;
//import com.miniproject.eventastic.attendee.service.AttendeeService;
//import com.miniproject.eventastic.event.entity.Event;
//import com.miniproject.eventastic.event.service.EventService;
//import com.miniproject.eventastic.review.entity.Review;
//import com.miniproject.eventastic.review.entity.dto.ReviewSubmitRequestDto;
//import com.miniproject.eventastic.review.repository.ReviewRepository;
//import com.miniproject.eventastic.review.service.ReviewService;
//import com.miniproject.eventastic.users.entity.Users;
//import com.miniproject.eventastic.users.service.UsersService;
//import com.miniproject.eventastic.users.service.impl.UsersServiceImpl;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.ZoneOffset;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class ReviewServiceImplTest {
//
//  @Mock
//  private ReviewRepository reviewRepository;
//  @Mock
//  private AttendeeService attendeeService;
//  @Mock
//  private UsersService usersService;
//  @Mock
//  private EventService eventService;
//
//  @InjectMocks
//  private ReviewServiceImpl reviewService = new ReviewServiceImpl(reviewRepository, usersService,
//      eventService, attendeeService);
//
//  private final Users reviewer = new Users();
//  private final Event event = new Event();
//  private final Users organizer = new Users();
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//
//    reviewer.setId(1L);
//    reviewer.setUsername("reviewer");
//    reviewer.setPassword("password");
//    usersService.saveUser(reviewer);
//
//    organizer.setId(2L);
//    organizer.setUsername("organizer");
//    organizer.setPassword("password");
//    organizer.setIsOrganizer(true);
//    usersService.saveUser(organizer);
//
//    event.setId(1L);
//    event.setTitle("event");
//    event.setDescription("description");
//    event.setEventDate(LocalDate.now());
//    eventService.saveEvent(event);
//  }
//
//  @Test
//  public void testSubmitReview() {
//    Attendee attendee = new Attendee();
//    AttendeeId attendeeId = new AttendeeId();
//
//    attendeeId.setUserId(reviewer.getId());
//    attendeeId.setEventId(event.getId());
//    attendee.setAttendedAt(event.getEventDate());
//
//    event.setOrganizer(organizer);
//    attendee.setEvent(event);
//    attendee.setUser(reviewer);
//
//    ReviewSubmitRequestDto requestDto = new ReviewSubmitRequestDto();
//    requestDto.setEventId(1L);
//    requestDto.setReviewMsg("Great event!");
//    requestDto.setRating(5);
//
//    when(usersService.getCurrentUser()).thenReturn(reviewer);
//    when(eventService.getEventById(1L)).thenReturn(event);
//    when(attendeeService.findAttendee(new AttendeeId(reviewer.getId(), 1L))).thenReturn(Optional.of(attendee));
//    when(reviewRepository.save(any(Review.class))).thenReturn(new Review());
//
//    Review review = reviewService.submitReview(requestDto);
//
//    assertNotNull(review);
//    verify(reviewRepository, times(1)).save(any(Review.class));
//  }
//
//}
