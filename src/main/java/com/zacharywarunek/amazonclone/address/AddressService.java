package com.zacharywarunek.amazonclone.address;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.ADDRESS_NOT_FOUND;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.ADDRESS_UNAUTHORIZED;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.NULL_VALUES;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AddressService {

  protected final Log logger = LogFactory.getLog(getClass());
  AddressRepo addressRepo;
  AccountService accountService;

  public List<Address> getAll(Long account_id) throws EntityNotFoundException {
    return addressRepo.findByAccount(accountService.findById(account_id));
  }

  public Address create(Long account_id, Address address)
      throws BadRequestException, EntityNotFoundException {
    if (address.getAddress() == null
        || address.getCity() == null
        || address.getState() == null
        || address.getZipcode() == null
        || address.getFavorite() == null
        || address.getFirst_name() == null
        || address.getLast_name() == null) throw new BadRequestException(NULL_VALUES.label);
    address.setAccount(accountService.findById(account_id));
    return addressRepo.save(address);
  }

  @Transactional
  public Address update(Long account_id, Long address_id, Address addressDetails)
      throws EntityNotFoundException, UnauthorizedException {
    Account account = accountService.findById(account_id);
    Address address = findById(address_id);
    if (!address.getAccount().getId().equals(account.getId()))
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

  public Address getFavorite(Long account_id) throws EntityNotFoundException {
    return addressRepo
        .findFavoriteAddressByAccount(accountService.findById(account_id))
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format(ExceptionResponses.NO_FAVORITE_ADDRESS.label, account_id)));
  }

  public Address findById(Long addressId) throws EntityNotFoundException {
    return addressRepo
        .findById(addressId)
        .orElseThrow(
            () -> new EntityNotFoundException(String.format(ADDRESS_NOT_FOUND.label, addressId)));
  }

  public void setFavorite(Long account_id, Long address_id)
      throws EntityNotFoundException, UnauthorizedException {
    Address address = findById(address_id);
    if (!address.getAccount().getId().equals(account_id))
      throw new UnauthorizedException(
          String.format(ADDRESS_UNAUTHORIZED.label, address_id, account_id));
    addressRepo.resetFavorite(accountService.findById(account_id));
    addressRepo.setFavorite(address_id);
  }

  public void delete(Long addressId) throws EntityNotFoundException {
    addressRepo.delete(findById(addressId));
  }
}
