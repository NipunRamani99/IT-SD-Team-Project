package structures.basic.Units;

public class PurebladeEnforcer extends Unit {

    public static void purebladeEnforcerAbility(PurebladeEnforcer purebladeEnforcer){
        purebladeEnforcer.setHealth(purebladeEnforcer.getHealth() + 1);
        purebladeEnforcer.setAttack(purebladeEnforcer.getAttack() + 1);

    }

}
