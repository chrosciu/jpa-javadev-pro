package pl.training.jpa.shop.commons;

import org.javamoney.moneta.FastMoney;
import org.mapstruct.Mapper;

@Mapper
public interface FastMoneyMapper {

    default FastMoney toMoney(String value) {
        return value != null ? FastMoney.parse(value) : null;
    }

    default String toText(FastMoney value) {
        return value != null ? value.toString() : null;
    }

}
