package com.solpyra.domain.upload.file.controller;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.solpyra.common.constant.ApplicationMessage;
import com.solpyra.common.constant.ApplicationMessage.ErrorMessage;
import com.solpyra.common.dto.response.Response;
import com.solpyra.common.constant.Constant;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/file/v1")
@RequiredArgsConstructor
public class FileUploadController {

  public static final String SHARE_FOLDER = "10EYS_8IIMcAGKxWPLpg9nmNsxfAP3ctx";
  final Drive drive;
  final MessageSource messageSource;

  @PostMapping(value = "/upload")
  ResponseEntity<Response<String>> uploadFile(@RequestParam MultipartFile file) throws IOException {

    if (!Objects.equals(file.getContentType(), MimeTypeUtils.IMAGE_JPEG_VALUE) &&
        !Objects.equals(file.getContentType(), MimeTypeUtils.IMAGE_PNG_VALUE) &&
        !Objects.equals(file.getContentType(), MimeTypeUtils.IMAGE_GIF_VALUE)) {
      throw new BadRequestException(
          ApplicationMessage.ErrorMessage.FILE_UPLOAD_NOT_VALID_TYPE);
    }

    File fileMetadata = new File();
    fileMetadata.setName(String.format("%s.%s", UUID.randomUUID(), file.getOriginalFilename()));
    fileMetadata.setMimeType(Constant.MEDIA);
    fileMetadata.setParents(List.of(SHARE_FOLDER));

    try {
      Permission permission = new Permission()
          .setType("anyone")
          .setRole("reader");

      File googleFile = drive.files()
          .create(fileMetadata,
              new InputStreamContent(
                  file.getContentType(),
                  new ByteArrayInputStream(file.getBytes())))
          .setSupportsAllDrives(true)
          .setFields(String.join(",", "id", "webViewLink", "parents"))
          .execute();

      drive.permissions().create(googleFile.getId(), permission).execute();

      return ResponseEntity.ok(Response.<String>builder()
          .traceId(MDC.get(Constant.TRACE_ID))
          .data(googleFile.getId())
          .build());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Response.<String>builder()
              .traceId(UUID.randomUUID().toString())
              .extraMessage(Map.of(ErrorMessage.FILE_UPLOAD_FAIL,
                  messageSource.getMessage(ErrorMessage.INVAlID_TOKEN, null, Locale.getDefault())))
              .build());
    }
  }


}
