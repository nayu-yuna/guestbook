package com;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuestbookService {

  private final S3Client s3Client;
  private final GuestbookRepository guestbookRepository;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Value("${aws.s3.base-url}")
  private String baseUrl;

  public GuestbookResponseDto uploadFile(MultipartFile file, String name, String title, String content) {
    log.info("Uploading file: {}", file != null ? file.getOriginalFilename() : "No file uploaded");

    String imageUrl = null;

    if (file != null && !file.isEmpty()) {
      String fileName = file.getOriginalFilename();
      String contentType = file.getContentType();
      long size = file.getSize();

      String s3Key = UUID.randomUUID() + "-" + fileName;

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Key)
          .contentType(contentType)
          .build();

      try {
        s3Client.putObject(putObjectRequest,
            RequestBody.fromInputStream(file.getInputStream(), size));
      } catch (IOException e) {
        log.error("Failed to upload file to S3", e);
        throw new RuntimeException("Failed to upload file to S3", e);
      }

      imageUrl = baseUrl + "/" + s3Key;
      log.info("File uploaded successfully: {}", s3Key);
    } else {
      log.info("No file uploaded. Skipping file upload.");
    }

    Guestbook guestbook = new Guestbook(name, title, content, imageUrl);

    guestbook = guestbookRepository.save(guestbook);

    return new GuestbookResponseDto(
        guestbook.getId(),
        guestbook.getName(),
        guestbook.getTitle(),
        guestbook.getContent(),
        guestbook.getImageUrl(),
        guestbook.getCreatedAt()
    );
  }

  public GuestbookResponseDto getGuestbooksById(Long id) {
    log.info("Fetching file with id: {}", id);

    Guestbook guestbook = guestbookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("File not found with id: " + id));

    return new GuestbookResponseDto(
        guestbook.getId(),
        guestbook.getName(),
        guestbook.getTitle(),
        guestbook.getContent(),
        guestbook.getImageUrl(),
        guestbook.getCreatedAt()
    );
  }

  public Page<GuestbookResponseDto> getGuestbooks(int page, int size){
    log.info("Fetching guestbook list - page: {}, size: {}", page, size);

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
    Page<Guestbook> guestbooks = guestbookRepository.findAll(pageable);

    return guestbooks.map(guestbook -> new GuestbookResponseDto(
        guestbook.getId(),
        guestbook.getName(),
        guestbook.getTitle(),
        guestbook.getContent(),
        guestbook.getImageUrl(),
        guestbook.getCreatedAt()
    ));
  }
}
