package com;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/guestbooks")
@Slf4j
@RequiredArgsConstructor
public class GuestbookController {

  private final GuestbookService guestbookService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<GuestbookResponseDto> uploadFile(
      @RequestParam("name") String name,
      @RequestParam("title") String title,
      @RequestParam("content") String content,
      @RequestParam(value = "image", required = false) MultipartFile file){

    log.info("Received file upload request");

    GuestbookResponseDto response = guestbookService.uploadFile(file, name, title, content);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<PageResponseDto> getGuestbooks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {

    log.info("Fetching guestbooks with pagination - page: {}, size: {}", page, size);

    Page<GuestbookResponseDto> guestbooks = guestbookService.getGuestbooks(page, size);

    PageResponseDto response = new PageResponseDto(
        guestbooks.getContent(),
        guestbooks.getTotalPages(),
        guestbooks.getTotalElements(),
        guestbooks.getSize(),
        guestbooks.getNumber()
    );

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GuestbookResponseDto> getFileById(
      @PathVariable Long id) {

    log.info("Received file retrieval request");

    GuestbookResponseDto response = guestbookService.getGuestbooksById(id);

    return ResponseEntity.ok(response);
  }
}
