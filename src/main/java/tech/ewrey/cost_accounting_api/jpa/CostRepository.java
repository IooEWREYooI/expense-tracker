package tech.ewrey.cost_accounting_api.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.ewrey.cost_accounting_api.entity.Cost;

import java.util.List;
import java.util.UUID;

public interface CostRepository extends JpaRepository<Cost, UUID> {

    List<Cost> findAllByChatId(Long chatId);

}