package tech.ewrey.cost_accounting_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cost")
public class Cost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    private String name;

    private Integer amount;

    private LocalDateTime time;

    private Long chatId;

    @Override
    public String toString() {
        return name + " "  + amount + "руб в " + time.format(DateTimeFormatter.ofPattern("h:m"));
    }

    @PrePersist
    public void prePersist() {
        this.time = LocalDateTime.now();
    }
}