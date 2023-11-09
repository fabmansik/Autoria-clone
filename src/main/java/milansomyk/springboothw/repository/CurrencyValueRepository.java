package milansomyk.springboothw.repository;

import milansomyk.springboothw.dto.CurrencyValueDto;
import milansomyk.springboothw.entity.CurrencyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyValueRepository extends JpaRepository<CurrencyValue, Integer> {
    CurrencyValue findCurrencyValueByCcy(String ccy);
}
