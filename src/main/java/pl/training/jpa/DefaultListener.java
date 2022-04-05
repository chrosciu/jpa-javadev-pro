package pl.training.jpa;

import lombok.extern.java.Log;

@Log
public class DefaultListener {

    public void postLoad(Object entity) {
        log.info("### postLoad");
    }

}
