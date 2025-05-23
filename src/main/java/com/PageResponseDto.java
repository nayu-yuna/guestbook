package com;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto {
  private List<GuestbookResponseDto> content;
  private int totalPages;
  private long totalElements;
  private int size;
  private int number;
}
