package pl.training.jpa.extras;

public enum Player {

    CIRCLE, CROSS;

    public Player reverse() {
        return this == CIRCLE ? CROSS : CIRCLE;
    }

}
