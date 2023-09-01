package dev.skamdem.patentsinvestor.models.dto;

import dev.skamdem.patentsinvestor.models.Stock;
import dev.skamdem.patentsinvestor.models.Tag;

import javax.validation.constraints.NotNull;

/**
 * Created by kamdem
 */
public class StockTagDTO {
    @NotNull
    private Stock stock;

    @NotNull
    private Tag tag;

    public StockTagDTO() {
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
