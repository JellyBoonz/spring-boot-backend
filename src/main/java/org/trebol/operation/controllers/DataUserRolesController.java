package org.trebol.operation.controllers;

import java.util.Map;

import javax.validation.Valid;

import io.jsonwebtoken.lang.Maps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.trebol.operation.GenericDataCrudController;
import org.trebol.pojo.DataPagePojo;
import org.trebol.operation.GenericDataController;
import org.trebol.pojo.ProductPojo;
import org.trebol.pojo.UserRolePojo;
import org.trebol.config.OperationProperties;
import org.trebol.jpa.entities.UserRole;
import org.trebol.exceptions.EntityAlreadyExistsException;
import org.trebol.jpa.GenericJpaService;
import org.trebol.operation.IDataCrudController;
import org.trebol.exceptions.BadInputException;

import com.querydsl.core.types.Predicate;

import javassist.NotFoundException;

/**
 * Controller that maps API resources for CRUD operations on UserRoles
 *
 * @author Benjamin La Madrid <bg.lamadrid at gmail.com>
 */
@RestController
@RequestMapping("/data/user_roles")
@PreAuthorize("isAuthenticated()")
public class DataUserRolesController
  extends GenericDataCrudController<UserRolePojo, UserRole> {

  @Autowired
  public DataUserRolesController(OperationProperties globals,
                                 GenericJpaService<UserRolePojo, UserRole> crudService) {
    super(globals, crudService);
  }

  @GetMapping({"", "/"})
  @PreAuthorize("hasAuthority('user_roles:read')")
  public DataPagePojo<UserRolePojo> readMany(@RequestParam Map<String, String> allRequestParams) {
    return super.readMany(null, null, allRequestParams);
  }

  @Override
  @PostMapping({"", "/"})
  @PreAuthorize("hasAuthority('user_roles:create')")
  public void create(@Valid @RequestBody UserRolePojo input) throws BadInputException, EntityAlreadyExistsException {
    super.create(input);
  }

  @Override
  @PutMapping({"", "/"})
  @PreAuthorize("hasAuthority('user_roles:update')")
  public void update(@RequestBody UserRolePojo input, @RequestParam Map<String, String> requestParams)
      throws BadInputException, NotFoundException {
    super.update(input, requestParams);
  }

  @Override
  @DeleteMapping({"", "/"})
  @PreAuthorize("hasAuthority('user_roles:delete')")
  public void delete(@RequestParam Map<String, String> requestParams) throws NotFoundException {
    super.delete(requestParams);
  }

  @Deprecated
  @GetMapping({"/{code}", "/{code}/"})
  @PreAuthorize("hasAuthority('user_roles:read')")
  public UserRolePojo readOne(@PathVariable String code) throws NotFoundException {
    Map<String, String> codeMatcher = Maps.of("code", code).build();
    Predicate matchesCode = crudService.parsePredicate(codeMatcher);
    return crudService.readOne(matchesCode);
  }

  @Deprecated
  @PutMapping({"/{code}", "/{code}/"})
  @PreAuthorize("hasAuthority('user_roles:update')")
  public void update(@RequestBody UserRolePojo input, @PathVariable String code)
    throws BadInputException, NotFoundException {
    Long userRoleId = this.readOne(code).getId();
    crudService.update(input, userRoleId);
  }

  @Deprecated
  @DeleteMapping({"/{code}", "/{code}/"})
  @PreAuthorize("hasAuthority('user_roles:delete')")
  public void delete(@PathVariable String code) throws NotFoundException {
    Long userRoleId = this.readOne(code).getId();
    crudService.delete(userRoleId);
  }
}
