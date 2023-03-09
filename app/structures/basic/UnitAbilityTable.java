package structures.basic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UnitAbilityTable {
    private Map<String, List<UnitAbility>> unitAbilities = new HashMap<>();
    public UnitAbilityTable() {
        generateTable();
    }

    private void generateTable() {
        unitAbilities.put("Comodo Charger", Collections.emptyList());
        unitAbilities.put("Hailstone Golem", Collections.emptyList());
        unitAbilities.put("Pureblade Enforcer", List.of(UnitAbility.BUFF_UNIT_ON_ENEMY_SPELL));
        unitAbilities.put("Azure Herald", List.of(UnitAbility.HEAL_AVATAR_ON_SUMMON));
        unitAbilities.put("Silverguard Knight", List.of(UnitAbility.PROVOKE,UnitAbility.BUFF_UNIT_ON_AVATAR_DAMAGE));
        unitAbilities.put("Azurite Lion", List.of(UnitAbility.ATTACK_TWICE));
        unitAbilities.put("Fire Spitter", List.of(UnitAbility.RANGED));
        unitAbilities.put("Ironcliff Guardian", List.of(UnitAbility.SUMMON_ANYWHERE, UnitAbility.PROVOKE));
        unitAbilities.put("Planar Scout", List.of(UnitAbility.SUMMON_ANYWHERE));
        unitAbilities.put("Rock Pulveriser", List.of(UnitAbility.PROVOKE));
        unitAbilities.put("Pyromancer", List.of(UnitAbility.RANGED));
        unitAbilities.put("Bloodshard Golem", Collections.emptyList());
        unitAbilities.put("Blaze Hound", List.of(UnitAbility.DRAW_CARD_ON_SUMMON));
        unitAbilities.put("Windshirke", List.of(UnitAbility.FLYING, UnitAbility.DRAW_CARD_ON_DEATH));
        unitAbilities.put("Serpenti", List.of(UnitAbility.ATTACK_TWICE));
    }

    public List<UnitAbility> getUnitAbilities(Unit unit) {
        return unitAbilities.get(unit.getName());
    }

    public List<UnitAbility> getUnitAbilities(String name) {
        return unitAbilities.get(name);
    }
}
