package pl.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import pl.training.jpa.TestUtils.Task;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UpdatePaymentTask implements Task {

    private final String paymentId;
    private final BigDecimal newValue;
    private final LockModeType lockModeType;
    private final long sleepBeforeLock;
    private final long sleepBeforeUpdate;

    private EntityManager entityManager;
    private CountDownLatch countDownLatch;

    public UpdatePaymentTask(String paymentId, BigDecimal newValue, LockModeType lockModeType, long sleepBeforeLock, long sleepBeforeUpdate) {
        this.paymentId = paymentId;
        this.newValue = newValue;
        this.lockModeType = lockModeType;
        this.sleepBeforeLock = sleepBeforeLock;
        this.sleepBeforeUpdate = sleepBeforeUpdate;
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();
        try {
            var transaction = entityManager.getTransaction();
            transaction.begin();
            TimeUnit.SECONDS.sleep(sleepBeforeLock);
            System.out.println("### " + threadName + " before lock/load");
            var payment = entityManager.find(Payment.class, paymentId, lockModeType);
            System.out.println("### " + threadName + " after lock/load");
            TimeUnit.SECONDS.sleep(sleepBeforeUpdate);
            payment.updateValue(newValue);
            System.out.println("### " + threadName + " before commit");
            transaction.commit();
            System.out.println("### " + threadName + " after commit");
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            entityManager.close();
            countDownLatch.countDown();
        }
    }

}
