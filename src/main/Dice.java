package main;

import java.util.List;

public class Dice {
    private Integer dieOne;
    private Integer dieTwo;

    public Dice (List<Integer> recentRoll) {
        if (recentRoll.size() > 0) {
            dieOne = recentRoll.get(0);
        }
        if (recentRoll.size() > 1) {
            dieTwo = recentRoll.get(1);
        }
    }
    public Integer getDieOne() {
        return dieOne;
    }

    public void setDieOne(Integer dieOne) {
        this.dieOne = dieOne;
    }

    public Integer getDieTwo() {
        return dieTwo;
    }

    public void setDieTwo(Integer dieTwo) {
        this.dieTwo = dieTwo;
    }
}
