package com.solpyra.entities;

import com.solpyra.common.constant.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "users",
    indexes = {@Index(columnList = "user_name", name = "idx_username")},
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_name"}, name = "uk_user_name"),
    })
public class Users extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @Column(name = "user_name", length = 30)
  private String userName;

  @Column(length = 500)
  private String password;

  private String email;

  @Column(name = "phone_number", length = 30)
  private String phoneNumber;

  @Column(name = "gender", length = 6)
  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String otpSecret;

  private boolean haveMfa;

  private boolean isSSOUser;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date")
  private Date createDate;

  @Temporal(TemporalType.DATE)
  @Column(name = "update_date")
  private Date updateDate;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "role_id")
  private Role role;

  @JoinColumn(name = "is_deleted")
  private boolean isDeleted;

}
