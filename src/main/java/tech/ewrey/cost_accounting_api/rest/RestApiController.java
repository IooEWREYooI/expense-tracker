package tech.ewrey.cost_accounting_api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.ewrey.cost_accounting_api.entity.Cost;
import tech.ewrey.cost_accounting_api.entity.Settings;
import tech.ewrey.cost_accounting_api.jpa.CostRepository;
import tech.ewrey.cost_accounting_api.jpa.SettingsRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/accounting")
public class RestApiController {

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @PostMapping("/cost")
    public ResponseEntity<Cost> createOrUpdateCost(@RequestBody Cost cost) {
        return ResponseEntity.ok(costRepository.save(cost));
    }

    @GetMapping("/cost")
    public ResponseEntity<List<Cost>> getCosts(@Param("date") LocalDate date) {
        var costs = costRepository.findAll();
        return ResponseEntity.ok(date != null
                ? costs.stream()
                    .filter(x -> x.getTime().getDayOfYear() == date.getDayOfYear())
                    .toList()
                : costs);
    }

    @PostMapping("/settings")
    public ResponseEntity<Settings> createSetting(@RequestBody Settings settings) {
        settingsRepository.deleteAll();
        return ResponseEntity.ok(settingsRepository.save(settings));
    }

    @GetMapping("/settings")
    public ResponseEntity<Settings> getSettings() {
        var list = settingsRepository.findAll();
        if (!list.isEmpty()) {
            return ResponseEntity.ok(settingsRepository.findAll().get(0));
        }

        return ResponseEntity.notFound().build();
    }

}
