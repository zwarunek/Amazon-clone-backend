package com.zacharywarunek.amazonclone.seller;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.SELLER_NOT_FOUND;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {

  private SellerRepo sellerRepo;

  public List<Seller> getAll() {
    return sellerRepo.findAll(Sort.by(Direction.ASC, "name"));
  }

  public Seller findById(Long sellerId) throws EntityNotFoundException {
    return sellerRepo
        .findById(sellerId)
        .orElseThrow(
            () -> new EntityNotFoundException(String.format(SELLER_NOT_FOUND.label, sellerId)));
  }

  public Seller create(Seller sellerDetails) throws BadRequestException {
    if (sellerDetails.getName() == null)
      throw new BadRequestException(ExceptionResponses.NULL_VALUES.label);
    return sellerRepo.save(sellerDetails);
  }

  @Transactional
  public Seller update(Long sellerId, Seller sellerDetails) throws EntityNotFoundException {
    Seller seller = findById(sellerId);
    if (sellerDetails.getName() != null) {
      seller.setName(sellerDetails.getName());
    }
    return seller;
  }

  public void delete(Long sellerId) throws EntityNotFoundException {
    sellerRepo.delete(findById(sellerId));
  }
}
