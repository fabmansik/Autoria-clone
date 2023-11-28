package milansomyk.springboothw.repository;

import milansomyk.springboothw.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Optional<Currency> findCurrencyByCcy(String ccy);

    List<Currency> findAll();
}
