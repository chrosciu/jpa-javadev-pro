package pl.training.jpa;

import jakarta.persistence.AttributeConverter;
import org.javamoney.moneta.FastMoney;

public class FastMoneyConverter implements AttributeConverter<FastMoney, String> {

    @Override
    public String convertToDatabaseColumn(FastMoney value) {
        return value != null ? value.toString() : null;
    }

    @Override
    public FastMoney convertToEntityAttribute(String text) {
        return text != null ? FastMoney.parse(text) : null;
    }

}
