package com;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookResponseDto {
  private Long id;
  private String name;
  private String title;
  private String content;
  private String imageUrl;
  private LocalDateTime createdAt;
}
