package org.trebol.jpa.services;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import org.trebol.jpa.entities.QSalesperson;

import org.trebol.pojo.PersonPojo;
import org.trebol.pojo.SalespersonPojo;
import org.trebol.exceptions.BadInputException;
import org.trebol.jpa.entities.Person;
import org.trebol.jpa.entities.Salesperson;
import org.trebol.jpa.GenericJpaCrudService;
import org.trebol.jpa.repositories.ISalespeopleJpaRepository;

/**
 *
 * @author Benjamin La Madrid <bg.lamadrid at gmail.com>
 */
@Transactional
@Service
public class SalespeopleJpaCrudServiceImpl
  extends GenericJpaCrudService<SalespersonPojo, Salesperson> {

  private static final Logger logger = LoggerFactory.getLogger(SalespeopleJpaCrudServiceImpl.class);
  private final ISalespeopleJpaRepository salespeopleRepository;
  private final GenericJpaCrudService<PersonPojo, Person> peopleService;

  @Autowired
  public SalespeopleJpaCrudServiceImpl(ISalespeopleJpaRepository repository,
    GenericJpaCrudService<PersonPojo, Person> peopleService) {
    super(repository);
    this.salespeopleRepository = repository;
    this.peopleService = peopleService;
  }

  @Override
  public SalespersonPojo convertToPojo(Salesperson source) {
    SalespersonPojo target = new SalespersonPojo();
    target.setId(source.getId());
    PersonPojo targetPerson = peopleService.convertToPojo(source.getPerson());
    target.setPerson(targetPerson);
    return target;
  }

  @Override
  public Salesperson convertToNewEntity(SalespersonPojo source) throws BadInputException {
    Salesperson target = new Salesperson();
    Person targetPerson = peopleService.convertToNewEntity(source.getPerson());
    target.setPerson(targetPerson);
    return target;
  }

  @Override
  public void applyChangesToExistingEntity(SalespersonPojo source, Salesperson target) throws BadInputException {
    Person targetPerson = target.getPerson();
    PersonPojo sourcePerson = source.getPerson();
    if (sourcePerson == null) {
      throw new BadInputException("Salesperson must have a person profile");
    }
    peopleService.applyChangesToExistingEntity(sourcePerson, targetPerson);
  }

  @Override
  public Predicate parsePredicate(Map<String, String> queryParamsMap) {
    logger.debug("queryParamsMapToPredicate({})", queryParamsMap);
    QSalesperson qSalesperson = QSalesperson.salesperson;
    BooleanBuilder predicate = new BooleanBuilder();
    for (String paramName : queryParamsMap.keySet()) {
      String stringValue = queryParamsMap.get(paramName);
      try {
        switch (paramName) {
          case "id":
            return predicate.and(qSalesperson.id.eq(Long.valueOf(stringValue))); // id matching is final
          case "name":
            predicate.and(qSalesperson.person.idNumber.eq(stringValue));
            break;
          case "idNumber":
            predicate.and(qSalesperson.person.idNumber.eq(stringValue));
            break;
          case "email":
            predicate.and(qSalesperson.person.idNumber.eq(stringValue));
            break;
          case "nameLike":
            predicate.and(qSalesperson.person.name.likeIgnoreCase("%" + stringValue + "%"));
            break;
          case "idNumberLike":
            predicate.and(qSalesperson.person.idNumber.likeIgnoreCase("%" + stringValue + "%"));
            break;
          case "emailLike":
            predicate.and(qSalesperson.person.email.likeIgnoreCase("%" + stringValue + "%"));
            break;
          default:
            break;
        }
      } catch (NumberFormatException exc) {
        logger.info("Param '{}' couldn't be parsed as number (value: '{}')", paramName, stringValue);
      }
    }

    return predicate;
  }

  @Override
  public Optional<Salesperson> getExisting(SalespersonPojo input) throws BadInputException {
    PersonPojo person = input.getPerson();
    if (person == null) {
      throw new BadInputException("Salesperson does not have profile information");
    } else {
      String idNumber = person.getIdNumber();
      if (idNumber == null) {
        throw new BadInputException("Salesperson does not have an ID card");
      } else {
        return salespeopleRepository.findByPersonIdNumber(idNumber);
      }
    }
  }
}
