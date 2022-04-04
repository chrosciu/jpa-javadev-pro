package pl.training.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class Fixtures {

    private static final String CURRENCY_CODE = "PLN";

    static String uuid() {
        return UUID.randomUUID().toString();
    }

    static Client testClient() {
        var client = new Client();
        client.setFirstName("Jan");
        client.setLastName("Kowalski");
        return client;
    }

    static Money testMoney(BigDecimal value) {
        var money = new Money();
        money.setValue(value);
        money.setCurrencyCode(CURRENCY_CODE);
        return money;
    }

    static PaymentId testPaymentId() {
        var paymentId = new PaymentId();
        paymentId.setId(Fixtures.uuid());
        paymentId.setExternalTransactionId(Fixtures.uuid());
        return paymentId;
    }

    static Payment testPayment(BigDecimal value) {
        var payment = new Payment();
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setTimestamp(LocalDateTime.now());
        payment.setDescription("Test description");
        payment.setChecksum("ESDWW222");
        // payment.setValue(FastMoney.of(value, CURRENCY_CODE));
        payment.setMoney(testMoney(value));
        payment.setEvents(List.of("STARTED", "VALIDATED", "PROCESSED"));
        payment.setProperties(Map.of("cardNumber", "1234567890", "cvv", "255"));
        return payment;
    }

}
