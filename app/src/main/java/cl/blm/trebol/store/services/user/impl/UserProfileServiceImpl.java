package cl.blm.trebol.store.services.user.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.blm.trebol.store.jpa.entities.Person;
import cl.blm.trebol.store.jpa.entities.User;
import cl.blm.trebol.store.jpa.repositories.PeopleRepository;
import cl.blm.trebol.store.jpa.repositories.UsersRepository;
import cl.blm.trebol.store.services.user.UserProfileService;

@Service
public class UserProfileServiceImpl
    implements UserProfileService {

  private final UsersRepository usersRepository;
  private final PeopleRepository peopleRepository;

  @Autowired
  public UserProfileServiceImpl(UsersRepository usersRepository, PeopleRepository peopleRepository) {
    super();
    this.usersRepository = usersRepository;
    this.peopleRepository = peopleRepository;
  }

  @Override
  public Person getProfileFromUserName(String userName) {
    Optional<User> userByName = usersRepository.findByNameWithProfile(userName);
    Person target = userByName.get().getPerson();
    return target;
  }

  @Override
  public boolean updateProfile(Person profile) {
    peopleRepository.saveAndFlush(profile);
    return true;
  }
}