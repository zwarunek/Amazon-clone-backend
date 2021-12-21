package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.*;

@Service
@AllArgsConstructor
public class AddressService {

  protected final Log logger = LogFactory.getLog(getClass());
  AddressRepo addressRepo;
  AccountRepo accountRepo;

  public List<Address> getAllAddresses(Long account_id) throws EntityNotFoundException {
    return addressRepo.findAddressByAccount(getAccountById(account_id));
  }

  public Address createAddress(Long account_id, Address address)
      throws BadRequestException, EntityNotFoundException {
    if (address.getAddress() == null
        || address.getCity() == null
        || address.getState() == null
        || address.getZipcode() == null
        || address.getFavorite() == null
        || address.getFirst_name() == null
        || address.getLast_name() == null) throw new BadRequestException(NULL_VALUES.label);
    address.setAccount(getAccountById(account_id));
    return addressRepo.save(address);
  }

  @Transactional
  public Address updateAddress(Long account_id, Long address_id, Address addressDetails)
      throws EntityNotFoundException, UnauthorizedException {
    Account account = getAccountById(account_id);
    Address address =
        addressRepo
            .findById(address_id)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(ADDRESS_NOT_FOUND.label, address_id)));
    if (address.getAccount().getId().equals(account.getId()))
      throw new UnauthorizedException(
          String.format(ADDRESS_UNAUTHORIZED.label, address_id, account_id));
    if (addressDetails.getAddress() != null) address.setAddress(addressDetails.getAddress());
    if (addressDetails.getCity() != null) address.setCity(addressDetails.getCity());
    if (addressDetails.getState() != null) address.setState(addressDetails.getState());
    if (addressDetails.getZipcode() != null) address.setZipcode(addressDetails.getZipcode());
    if (addressDetails.getFirst_name() != null)
      address.setFirst_name(addressDetails.getFirst_name());
    if (addressDetails.getLast_name() != null) address.setLast_name(addressDetails.getLast_name());
    return address;
  }

  private Account getAccountById(Long account_id) throws EntityNotFoundException {
    return accountRepo
        .findById(account_id)
        .orElseThrow(
            () ->
                new EntityNotFoundException(String.format(ACCOUNT_ID_NOT_FOUND.label, account_id)));
  }

  public Address getFavorite(Long account_id) throws EntityNotFoundException {
    return addressRepo
        .findFavoriteAddressByAccount(getAccountById(account_id))
        .orElseThrow(
            () ->
                new javax.persistence.EntityNotFoundException(
                    String.format(ExceptionResponses.NO_FAVORITE_ADDRESS.label, account_id)));
  }

  public void setFavorite(Long account_id, Long address_id) throws EntityNotFoundException {
    addressRepo.resetFavorite(getAccountById(account_id));
    addressRepo.setFavorite(address_id);
  }
}
