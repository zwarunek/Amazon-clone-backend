package com.zacharywarunek.amazonclone.category;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.CATEGORY_NOT_FOUND;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CategoryService {

  private CategoryRepo categoryRepo;

  public List<Category> getAll() {
    return categoryRepo.findAll(Sort.by(Direction.ASC, "name"));
  }

  public Category findById(Long categoryId) throws EntityNotFoundException {
    return categoryRepo
        .findById(categoryId)
        .orElseThrow(
            () -> new EntityNotFoundException(String.format(CATEGORY_NOT_FOUND.label, categoryId)));
  }

  public Category create(Category categoryDetails) throws BadRequestException {
    if (categoryDetails.getName() == null)
      throw new BadRequestException(ExceptionResponses.NULL_VALUES.label);
    return categoryRepo.save(categoryDetails);
  }

  @Transactional
  public Category update(Long categoryId, Category categoryDetails) throws EntityNotFoundException {
    Category category = findById(categoryId);
    if (categoryDetails.getName() != null) {
      category.setName(categoryDetails.getName());
    }
    return category;
  }

  public void delete(Long categoryId) throws EntityNotFoundException {
    categoryRepo.delete(findById(categoryId));
  }
}
