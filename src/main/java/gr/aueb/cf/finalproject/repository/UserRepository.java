package gr.aueb.cf.finalproject.repository;

import gr.aueb.cf.finalproject.model.User;
import gr.aueb.cf.finalproject.model.static_data.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByCountry(Country country);
}
