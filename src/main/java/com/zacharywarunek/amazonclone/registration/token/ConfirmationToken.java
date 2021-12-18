package com.zacharywarunek.amazonclone.registration.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zacharywarunek.amazonclone.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "confirmation_token")
public class ConfirmationToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created_at;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expires_at;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmed_at;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public ConfirmationToken(String token, LocalDateTime created_at, LocalDateTime expires_at, Account account) {
        this.token = token;
        this.created_at = created_at;
        this.expires_at = expires_at;
        this.account = account;
    }
}
