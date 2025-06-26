package models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account {
    private long id;
    private String name;
    private String description;
    private String currency;
    private long userId; // Зв'язок з користувачем

    public Account(String name, String description, String currency, long userId) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.userId = userId;
    }
}