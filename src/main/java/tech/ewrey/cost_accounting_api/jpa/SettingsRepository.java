package tech.ewrey.cost_accounting_api.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.ewrey.cost_accounting_api.entity.Settings;

import java.util.List;
import java.util.UUID;

public interface SettingsRepository extends JpaRepository<Settings, UUID> {

    List<Settings> findAllByChatId(Long chatId);

}