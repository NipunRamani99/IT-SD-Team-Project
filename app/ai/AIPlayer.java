package ai;

import structures.GameState;
import structures.Turn;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.basic.Unit;
import structures.statemachine.CardSelectedState;
import structures.statemachine.CastCard;
import structures.statemachine.EndTurnState;
import structures.statemachine.GameStateMachine;
import structures.statemachine.HumanAttackState;
import structures.statemachine.NoSelectionState;
import structures.statemachine.State;
import structures.statemachine.UnitMovingState;
import utils.Constants;

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
    public boolean searchAction(ActorRef out,GameState gameState, GameStateMachine gameStateMachine) {
        //Create a list of actions to be performed
        //Check if deck of cards has unit card
        if(canPlay) {
            Unit unit = gameState.aiUnit;
            gameState.targetTile=gameState.board.getTile(unit.getPosition().getTilex() - 3, unit.getPosition().getTiley());
            //get a card
            
            if(null!=gameState.targetTile.getUnit())
            {
            	aiCastCard(out, gameState, gameStateMachine);
            	State unitMove = new UnitMovingState(unit, gameState.board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()), gameState.board.getTile(unit.getPosition().getTilex() - 2, unit.getPosition().getTiley()));
            	State aiAttack = new HumanAttackState(unit, gameState.targetTile, false,false);
            	unitMove.setNextState(aiAttack);
            	nextAiMove.setNextState(unitMove);
            	canPlay = false;
            }
            else
            {
            	nextAiMove = new UnitMovingState(unit, gameState.board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()), gameState.board.getTile(unit.getPosition().getTilex() +1, unit.getPosition().getTiley()));
            	canPlay = false;
            }
         
            //Move unit towards enemies
           // canPlay = false;
            return false;
        } else {
            canPlay = true;
            return true;
        }
    }
    
    private void aiCastCard(ActorRef out, GameState gameState, GameStateMachine gameStateMachine)
    {
    	Tile chooseTile=Action.searchLowestAiUnitAttack(out, gameState);
    	System.out.println("Ai cast the card");
    	nextAiMove = new CardSelectedState(out, 1, chooseTile, gameState);
           
    }

    public State getNextAiMove() {
        return nextAiMove;
    }
}
