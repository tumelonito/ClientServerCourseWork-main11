package models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Asset {
    private long id;
    private String name;
    private String ticker;
    private String description;
    private String sector;
    private BigDecimal currentPrice;

    public Asset(String name, String ticker, String description, String sector) {
        this.name = name;
        this.ticker = ticker;
        this.description = description;
        this.sector = sector;
    }
}