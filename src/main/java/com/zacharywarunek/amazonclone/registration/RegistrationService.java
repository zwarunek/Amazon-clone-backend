package com.zacharywarunek.amazonclone.registration;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.email.EmailService;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AccountService accountService;
    private final AccountRepo accountRepo;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    public ResponseEntity<Object> register(RegistrationRequest request) {

        String token = accountService.register(new Account(request.getFirstName(),
                                                           request.getLastName(),
                                                           request.getUsername(),
                                                           request.getPassword(),
                                                           AccountRole.ROLE_USER

        ));

        String link = System.getenv("URL") + "/api/v1/registration/confirm?token=" + token;
        emailService.send(request.getUsername(), buildEmail(request.getFirstName(), link));
        return ResponseEntity.ok().body("Registration Successful: Email confirmation sent");
    }

    @Transactional
    public ResponseEntity<Object> confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "token not found"));
        if(confirmationToken.getCreated_at().isAfter(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is invalid");
        if(!accountRepo.checkIfUsernameExists(confirmationToken.getAccount().getUsername()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                              "Account with username " + confirmationToken.getAccount().getUsername() +
                                                      " doesn't exist");
        if(confirmationToken.getConfirmed_at() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email already confirmed");
        if(confirmationToken.getExpires_at().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is expired");
        confirmationTokenService.setConfirmedAt(token);
        accountService.enableAccount(confirmationToken.getAccount().getUsername());
        return ResponseEntity.ok().body("Confirmation Successful: Account has been confirmed");
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" + "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" + "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" + "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" + "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" + "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" + "                  </tr>\n" + "                </tbody></table>\n" +
                "              </a>\n" + "            </td>\n" + "          </tr>\n" + "        </tbody></table>\n" +
                "        \n" + "      </td>\n" + "    </tr>\n" + "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" + "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" + "                </tbody></table>\n" + "        \n" + "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" + "    </tr>\n" +
                "  </tbody></table>\n" + "\n" + "\n" + "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" + "      <td height=\"30\"><br></td>\n" + "    </tr>\n" + "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name +
                ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" +
                link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" + "      </td>\n" + "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" + "    <tr>\n" + "      <td height=\"30\"><br></td>\n" + "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" + "\n" + "</div></div>";
    }
}
