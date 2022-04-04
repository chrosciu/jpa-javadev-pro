package pl.training.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UpdatePaymentTask implements TestUtils.Task {

    private final String paymentId;
    private final BigDecimal newValue;
    private final LockModeType lockModeType;
    private final long sleepBeforeLock;
    private final long sleepAfterLock;

    private EntityManager entityManager;
    private CountDownLatch countDownLatch;

    public UpdatePaymentTask(String paymentId, BigDecimal newValue, LockModeType lockModeType, long sleepBeforeLock, long sleepAfterLock) {
        this.paymentId = paymentId;
        this.newValue = newValue;
        this.lockModeType = lockModeType;
        this.sleepBeforeLock = sleepBeforeLock;
        this.sleepAfterLock = sleepAfterLock;
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
        System.out.println("### " + threadName + " started");
        try {
            var transaction = entityManager.getTransaction();
            transaction.begin();
            TimeUnit.SECONDS.sleep(sleepBeforeLock);
            System.out.println("### " + threadName + " before load");
            var payment = entityManager.find(Payment.class, paymentId, lockModeType);
            System.out.println("### " + threadName + " after load");
            TimeUnit.SECONDS.sleep(sleepAfterLock);
            payment.getMoney().setValue(newValue);
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
