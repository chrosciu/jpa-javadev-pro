package pl.training.jpa;

import jakarta.persistence.*;
import lombok.extern.java.Log;

@Log
public class PaymentListener {

    @PrePersist
    public void prePersist(Payment payment) {
        log.info("### prePersist");
    }

    @PostPersist
    public void postPersist(Payment payment) {
        log.info("### postPersist");
    }

    @PreUpdate
    public void preUpdate(Payment payment) {
        log.info("### preUpdate");
    }

    @PostUpdate
    public void postUpdate(Payment payment) {
        log.info("### postUpdate");
    }

    @PreRemove
    public void preRemove(Payment payment) {
        log.info("### preRemove");
    }

    @PostRemove
    public void postRemove(Payment payment) {
        log.info("### preRemove");
    }

    @PostLoad
    public void postLoad(Payment payment) {
        log.info("### postLoad");
    }

}
