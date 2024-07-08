package com.miniproject.eventastic.review.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSubmitRequestDto {

  private Integer rating;
  private String reviewMsg;

}
