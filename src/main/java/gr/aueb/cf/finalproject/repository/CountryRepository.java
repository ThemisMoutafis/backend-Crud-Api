package gr.aueb.cf.finalproject.repository;

import gr.aueb.cf.finalproject.model.static_data.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long>, JpaSpecificationExecutor<Country> {
    Optional<Country> findByName(String name);
}
