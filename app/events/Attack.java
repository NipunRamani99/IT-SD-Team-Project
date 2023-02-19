package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;
import structures.basic.*;

/**
 * This class will define the action of the unit attack, player attack, and attack between unit and player
 */
public class Attack implements EventProcessor {
    /**
     * This unit is for the friendly unit that will launch an attack
     */
    private Unit myUnit;
    /**
     * This unit is for the unit that will be attacked
     */
    private Unit enemyUnit;

    /**
     * This player is for the player 1 information
     */
    private Player player1;

    /**
     * This player is for the player 2 information
     */
    private Player player2;

    private Boolean canAttack = false;

    /**
     * This constructor is for the attack between two units
     * @param myUnit the unit refers to the attacking unit
     * @param enemyUnit the unit refer to the attacked unit
     */
    public Attack(Unit myUnit, Unit enemyUnit){
    	this.myUnit = myUnit;
        this.enemyUnit = enemyUnit;

    }

    /**
     * This constructor is for the attack between my unit and aviator of enemy player
     * @param myUnit the unit refers to any unit including attacking or attacked unit
     * @param player the player refer to any player including player1 or player2
     */
    public Attack(Unit myUnit, Player player){
        this.myUnit = myUnit;
        this.player1 = player;
        this.player2 = player;

    }


    /**
     * This constructor is for the attack between the aviators of player1 and player2
     * @param player1 The player refers to the attacking player
     * @param player2  The player refers to the attacked player
     */
    public Attack(Player player1, Player player2){
        this.player1 = player1;
        this.player2 = player2;
    }

    public void processEvent(ActorRef out, GameState gameState, JsonNode message)
    {

    }
}
