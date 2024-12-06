package tech.ewrey.cost_accounting_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "settings")
public class Settings {

    public Settings() {}

    public Settings(Integer limit, Long chatId) {
        this.limit = limit;
        this.chatId = chatId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "limit_")
    private Integer limit;

    @Column(name = "chatId")
    private Long chatId;

}