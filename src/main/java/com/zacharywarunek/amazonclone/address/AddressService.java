package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressService {

  protected final Log logger = LogFactory.getLog(getClass());
  AddressRepo addressRepo;
  AccountRepo accountRepo;

  public List<Address> getAllAddresses(Long account_id) {
    return addressRepo.findAddressByAccount(getAccountById(account_id));
  }

  public ResponseEntity<Object> createAddress(Long account_id, Address address) {
    if (address.getAddress() == null
        || address.getCity() == null
        || address.getState() == null
        || address.getZipcode() == null
        || address.getFavorite() == null
        || address.getFirst_name() == null
        || address.getLast_name() == null)
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "An error occurred when creating the address: " + "Null values present");
    address.setAccount(getAccountById(account_id));
    addressRepo.save(address);
    return ResponseEntity.ok("Address Successfully Created");
  }

  @Transactional
  public ResponseEntity<Object> updateAddress(
      Long account_id, Long address_id, Address addressDetails) {
    Account account = getAccountById(account_id);
    Optional<Address> optionalAddress = addressRepo.findById(address_id);
    if (!optionalAddress.isPresent())
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Address with id " + address_id + " not found");
    Address address = optionalAddress.get();
    if (address.getAccount().getId().equals(account.getId()))
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Address with id "
              + address_id
              + " does not "
              + "belong to account with id "
              + account_id);
    if (addressDetails.getAddress() != null) address.setAddress(addressDetails.getAddress());
    if (addressDetails.getCity() != null) address.setCity(addressDetails.getCity());
    if (addressDetails.getState() != null) address.setState(addressDetails.getState());
    if (addressDetails.getZipcode() != null) address.setZipcode(addressDetails.getZipcode());
    if (addressDetails.getFirst_name() != null)
      address.setFirst_name(addressDetails.getFirst_name());
    if (addressDetails.getLast_name() != null) address.setLast_name(addressDetails.getLast_name());
    return ResponseEntity.ok("Address Successfully Updated");
  }

  private Account getAccountById(Long account_id) {
    Optional<Account> optionalAccount = accountRepo.findById(account_id);
    if (!optionalAccount.isPresent())
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Account with id " + account_id + " not found");
    return optionalAccount.get();
  }

  public ResponseEntity<Address> getFavorite(Long account_id) {
    Optional<Address> optionalAddress =
        addressRepo.findFavoriteAddressByAccount(getAccountById(account_id));
    if (!optionalAddress.isPresent())
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Address with id " + account_id + " has no " + "favorite address");
    return ResponseEntity.ok(optionalAddress.get());
  }

  public ResponseEntity<Object> setFavorite(Long account_id, Long address_id) {
    if (!addressRepo.findById(address_id).isPresent())
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Address with id " + address_id + " not found");
    addressRepo.resetFavorite(getAccountById(account_id));
    addressRepo.setFavorite(address_id);
    return ResponseEntity.ok("Address with id " + address_id + " is now the favorite address");
  }
}
