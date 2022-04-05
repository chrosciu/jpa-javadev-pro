package pl.training.jpa.shop.payments.domain;

import org.mapstruct.factory.Mappers;
import pl.training.jpa.shop.payments.ports.GetPaymentUseCase;
import pl.training.jpa.shop.payments.ports.PaymentReader;
import pl.training.jpa.shop.payments.ports.PaymentsFactory;

public class DefaultPaymentsFactory implements PaymentsFactory {

    @Override
    public GetPaymentUseCase getPaymentUseCase(PaymentReader paymentReader) {
        var paymentsMapper = Mappers.getMapper(PaymentsDomainMapper.class);
        var paymentsReaderAdapter = new PaymentsReaderAdapter(paymentReader, paymentsMapper);
        var paymentsService = new PaymentService(paymentsReaderAdapter);
        return new GetPaymentUseCaseAdapter(paymentsService, paymentsMapper);
    }

}
