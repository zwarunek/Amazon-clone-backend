package com.zacharywarunek.amazonclone.registration;

import com.zacharywarunek.amazonclone.exceptions.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/registration")
public class RegistrationController {

  private final RegistrationService registrationService;

  @PostMapping
  public ResponseEntity<Object> register(@RequestBody RegistrationRequest request) {
    try {
      return ResponseEntity.ok(registrationService.register(request));
    } catch (UsernameTakenException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    } catch (BadRequestException e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping(path = "confirm")
  public ResponseEntity<Object> confirm(@RequestParam("token") String token) {

    try {
      return ResponseEntity.ok(registrationService.confirmToken(token));
    } catch (BadRequestException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (InvalidTokenException | ExpiredTokenException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
