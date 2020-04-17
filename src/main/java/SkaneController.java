import room.element.Skane;

public class SkaneController {
    private Skane ska;
    private int max_oxy;

    public SkaneController(Skane ska, int max_oxygen) {
        this.ska = ska;
        this.max_oxy = max_oxygen;
    }

    public void toggleBury() {
        if (ska.isBury())
            ska.bury(false);
        else if (ska.getOxygenLevel() == max_oxy)
            ska.bury(true);
    }

    public void inhale() {
        int oxy_lvl = ska.getOxygenLevel();
        if (oxy_lvl == 0)
            ska.bury(false);

        if (ska.isBury()) {
            ska.setOxygenLevel(oxy_lvl - 1);
        } else if (oxy_lvl < max_oxy) {
            if (oxy_lvl + max_oxy / 50 > max_oxy)
                ska.setOxygenLevel(max_oxy);
            else
                ska.setOxygenLevel(oxy_lvl + max_oxy / 50);
        }
    }

    public void takeDamage(int dmg) {
        if (dmg < 0) return;
        ska.takeDamage(dmg);
        for (int i = 0; i < dmg; ++i)
            ska.shrink();
    }

    public void nom(int nourishment) {
        if (nourishment < 0) return;
        ska.setHp(ska.getHp() + nourishment);
        for (int i = 0; i < nourishment; ++i)
            ska.grow();
    }
}
