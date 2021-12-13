package com.zacharywarunek.amazonclone.registration.token;

import com.zacharywarunek.amazonclone.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "confirmation_token")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime created_at;

    private LocalDateTime expires_at;

    private LocalDateTime confirmed_at;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public ConfirmationToken(String token,
                             LocalDateTime created_at,
                             LocalDateTime expires_at,
                             Account account) {
        this.token = token;
        this.created_at = created_at;
        this.expires_at = expires_at;
        this.account = account;
    }
}
