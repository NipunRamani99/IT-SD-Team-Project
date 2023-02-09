package ai;

import structures.basic.Tile;
import structures.basic.Unit;
/**
 * Action class is used to implement the different actions an AI can perform while playing the game.
 * Each action is defined by an action type, instances of tiles on the board,
 * and a hand position if the action involves casting a card from the AI's hand.
 */
public class Action {
    /**
     * Define the action type
     */
    private ActionType actionType = ActionType.None;
    /**
     * The tile refer to the first clicked tile or friend unit tile
     */
    private Tile startTile;
    /**
     * The tile refer to the destination tile or enemy unit tile
     */
    private Tile endTile;
    /**
     * To specify which card to use
     */
    private int handPosition = 0; //To specify which card to use

    /**
     * Constructor for actions like end turn.
     * @param actionType
     */
    public Action(ActionType actionType) {
        this.actionType = actionType;
    }

    /**
     * Constructor for double tile actions like unit move and unit attack.
     * @param actionType the action type like move or attack
     * @param startTile  the first tile needed to click, like the friend unit tile
     * @param endTile the destination tile needed to click, like the enemy unit tile or the moving destination tile
     */
    public Action(ActionType actionType, Tile startTile, Tile endTile) {
        this.actionType = actionType;
        this.startTile = startTile;
        this.endTile = endTile;
    }

    /**
     * Constructor for single tile actions like cast unit card or cast spell card.
     * @param actionType  this refers to  what kind action need to be performed
     * @param startTile  this refers to which tile need to be cast
     * @param handPosition this refers to which card in hand needed to be cast
     */
    public Action(ActionType actionType, Tile startTile, int handPosition) {
        this.actionType = actionType;
        this.startTile = startTile;
        this.handPosition = handPosition;
    }

    /**
     * This is the moving function
     * @param start the start tile that click first
     * @param end  the destination tile that need to move to
     */
    public void MoveUnit(Tile start, Tile end) {
        //Unit unit = start.getUnit();
        //Call the command to move the unit
        //BasicCommand.moveUnitToTile(out, unit, endTile);
        //clear the start tile
        //start.setUnit(null);
        //Update the target tile with the unit
        //end.setUnit(unit);
    }
}
