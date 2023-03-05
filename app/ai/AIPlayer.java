package ai;

import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.basic.Unit;
import structures.statemachine.EndTurnState;
import structures.statemachine.NoSelectionState;
import structures.statemachine.State;
import structures.statemachine.UnitMovingState;

/**
 * AIPlayer implements an AI which can analyse the positions on the board, calculate different actions,
 * and execute the actions one by one over the board. The actions it can perform includes moving a unit from one tile to another,
 * selecting a unit and then using it to attack an enemy unit, cast a card from its hand, etc.
 */
public class AIPlayer{

    private boolean canPlay = true;

    private State nextAiMove = null;

    public AIPlayer() {
        this.nextAiMove = new EndTurnState();

    }

    /**
     * This function is for AI to execute asynchronously
     */
    public void update() {
        //performAction();
    }

    /**
     * The search action method will analyze the AI's position on the board and then find appropriate actions it should perform.
     * The actions it can perform will be stored in the class variable 'actions'.
     */
    public boolean searchAction(GameState gameState) {
        //Create a list of actions to be performed
        //Check if deck of cards has unit card
        if(canPlay) {
            Unit unit = gameState.aiUnit;
            nextAiMove = new UnitMovingState(unit, gameState.board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()), gameState.board.getTile(unit.getPosition().getTilex() - 1, unit.getPosition().getTiley()));
            //Move unit towards enemies
            canPlay = false;
            return false;
        } else {
            canPlay = true;
            return true;
        }
    }

    public State getNextAiMove() {
        return nextAiMove;
    }
}
