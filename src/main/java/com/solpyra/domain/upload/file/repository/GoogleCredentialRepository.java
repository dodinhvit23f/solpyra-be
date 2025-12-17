package com.solpyra.domain.upload.file.repository;

import com.solpyra.entities.GoogleCredential;
import java.math.BigInteger;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoogleCredentialRepository extends JpaRepository<GoogleCredential, BigInteger> {

  @Query("""
  SELECT c
  FROM GoogleCredential c
  """)
  List<GoogleCredential> findByOne(Pageable pageable);
}
