package br.edu.utfpr.pb.pw45s.server.controller;

import br.edu.utfpr.pb.pw45s.server.dto.CategoryDto;
import br.edu.utfpr.pb.pw45s.server.model.Category;
import br.edu.utfpr.pb.pw45s.server.service.CategoryService;
import br.edu.utfpr.pb.pw45s.server.service.CrudService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
//@PreAuthorize("hasAnyRole('ADMIN')")
public class CategoryController extends CrudController<Category, CategoryDto, Long> {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;


    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        super(Category.class, CategoryDto.class);
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected CrudService<Category, Long> getService() {
        return this.categoryService;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return this.modelMapper;
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    // @PostAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid CategoryDto entity) {
        return super.create(entity);
    }

    @Override
    @DeleteMapping("{id}")
    @PreAuthorize("#id == authentication.principal.id or hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
}