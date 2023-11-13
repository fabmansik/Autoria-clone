package milansomyk.springboothw.repository;

import milansomyk.springboothw.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    Currency findCurrencyValueByCcy(String ccy);
}
