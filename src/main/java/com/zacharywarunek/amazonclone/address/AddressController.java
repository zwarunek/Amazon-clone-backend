package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/accounts/{account_id}/addresses")
public class AddressController {
  private final AddressService addressService;

  @PostMapping
  public ResponseEntity<Object> createAddress(
      @PathVariable("account_id") Long account_id, @RequestBody Address address) {
    try {
      return ResponseEntity.ok(addressService.createAddress(account_id, address));
    } catch (BadRequestException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping
  public List<Address> getAllAddresses(@PathVariable("account_id") Long account_id) {
    try {
      return addressService.getAllAddresses(account_id);
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping(path = "{address_id}")
  public ResponseEntity<Object> updateAddress(
      @PathVariable("account_id") Long account_id,
      @PathVariable("address_id") Long address_id,
      @RequestBody Address addressDetails) {
    try {
      return ResponseEntity.ok(
          addressService.updateAddress(account_id, address_id, addressDetails));
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (UnauthorizedException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
  }

  @GetMapping(path = "/favorite")
  public ResponseEntity<Address> getFavorite(@PathVariable("account_id") Long account_id) {
    Address address;
    try {
      address = addressService.getFavorite(account_id);
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
    return ResponseEntity.ok(address);
  }

  @PutMapping(path = "{address_id}/favorite")
  public ResponseEntity<String> setFavorite(
      @PathVariable("account_id") Long account_id, @PathVariable("address_id") Long address_id) {
    try {
      addressService.setFavorite(account_id, address_id);
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch(UnauthorizedException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
    return ResponseEntity.ok("Address with id " + address_id + " is now the favorite address");
  }
}
