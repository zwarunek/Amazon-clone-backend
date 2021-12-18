package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/accounts/{account_id}/addresses")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<Object> createAddress(@PathVariable("account_id") Long account_id,
                                             @RequestBody Address address){
        return addressService.createAddress(account_id, address);
    }

    @GetMapping
    public List<Address> getAllAddresses(@PathVariable("account_id") Long account_id){
        return addressService.getAllAddresses(account_id);
    }

    @PutMapping(path = "{address_id}")
    public ResponseEntity<Object> updateAddress(@PathVariable("account_id") Long account_id,
                                                @PathVariable("address_id") Long address_id,
                                                @RequestBody Address addressDetails) {
        return addressService.updateAddress(account_id, address_id, addressDetails);
    }

    @GetMapping(path = "/favorite")
    public ResponseEntity<Address> setFavorite(@PathVariable("account_id") Long account_id){
        return addressService.getFavorite(account_id);
    }

    @PutMapping(path = "{address_id}/favorite")
    public ResponseEntity<Object> setFavorite(@PathVariable("account_id") Long account_id,
                                               @PathVariable("address_id") Long address_id){
        return addressService.setFavorite(account_id, address_id);
    }
}
