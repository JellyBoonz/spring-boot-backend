/*
 * Copyright (c) 2022 The Trebol eCommerce Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.trebol.operation.services;

import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.trebol.exceptions.BadInputException;
import org.trebol.jpa.entities.*;
import org.trebol.jpa.repositories.ICustomersJpaRepository;
import org.trebol.jpa.repositories.IPeopleJpaRepository;
import org.trebol.jpa.repositories.IUserRolesJpaRepository;
import org.trebol.jpa.repositories.IUsersJpaRepository;
import org.trebol.operation.IRegistrationService;
import org.trebol.pojo.PersonPojo;
import org.trebol.pojo.RegistrationPojo;

import javax.persistence.EntityExistsException;
import java.util.Optional;

@Service
public class RegistrationServiceImpl
  implements IRegistrationService {

  private final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);
  private final IPeopleJpaRepository peopleRepository;
  private final IUsersJpaRepository usersRepository;
  private final IUserRolesJpaRepository rolesRepository;
  private final ICustomersJpaRepository customersRepository;
  private final PasswordEncoder passwordEncoder;
  private final ConversionService conversionService;

  @Autowired
  public RegistrationServiceImpl(IPeopleJpaRepository peopleRepository,
    IUsersJpaRepository usersRepository, IUserRolesJpaRepository rolesRepository,
    ICustomersJpaRepository customersRepository, PasswordEncoder passwordEncoder,
    ConversionService conversionService) {
    this.peopleRepository = peopleRepository;
    this.usersRepository = usersRepository;
    this.rolesRepository = rolesRepository;
    this.customersRepository = customersRepository;
    this.passwordEncoder = passwordEncoder;
    this.conversionService = conversionService;
  }

  @Override
  public void register(RegistrationPojo registration)
      throws BadInputException, EntityExistsException {
    String username = registration.getName();
    Predicate userWithSameName = QUser.user.name.eq(username);
    if (usersRepository.exists(userWithSameName)) {
      throw new EntityExistsException("That username is taken.");
    }

    PersonPojo sourcePerson = registration.getProfile();
    Person newPerson = conversionService.convert(sourcePerson, Person.class);
    if (newPerson == null) {
      throw new BadInputException("Input profile has insufficient or invalid data.");
    }

    Predicate sameProfileData = QPerson.person.idNumber.eq(newPerson.getIdNumber());
    if (peopleRepository.exists(sameProfileData)) {
      throw new EntityExistsException("That ID number is already registered and associated to an account.");
    } else {
      newPerson = peopleRepository.saveAndFlush(newPerson);
    }

    User newUser = this.convertToUser(registration);
    newUser.setPerson(newPerson);
    usersRepository.saveAndFlush(newUser);
    logger.info("New user created with name '{}' and idNumber '{}'", newUser.getName(), newPerson.getIdNumber());

    Customer newCustomer = new Customer();
    newCustomer.setPerson(newPerson);
    customersRepository.saveAndFlush(newCustomer);
  }

  protected User convertToUser(RegistrationPojo registration) {
    String password = passwordEncoder.encode(registration.getPassword());
    User target = new User();
    target.setName(registration.getName());
    target.setPassword(password);

    Optional<UserRole> customerRole = rolesRepository.findByName("Customer");
    if (customerRole.isEmpty()) {
      throw new IllegalStateException("No user role matches 'Customer', database might be compromised");
    } else {
      target.setUserRole(customerRole.get());
    }
    return target;
  }

}
