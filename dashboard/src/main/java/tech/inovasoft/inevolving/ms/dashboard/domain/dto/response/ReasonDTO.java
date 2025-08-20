package tech.inovasoft.inevolving.ms.dashboard.domain.dto.response;

import lombok.Data;

@Data
public class ReasonDTO {
    private String reason;
    private int amount;

    @Override
    public String toString() {
        return "{" +
                "Motivo='" + reason + '\'' +
                ", Quantidade de vezes que foi usado=" + amount +
                '}';
    }
}
