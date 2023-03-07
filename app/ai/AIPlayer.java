package ai;

import ai.actions.*;
//import ai.actions.Action;
import ai.*;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttribute;
import structures.GameState;
import structures.Turn;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.basic.Unit;
import structures.statemachine.CardSelectedState;
import structures.statemachine.CastCard;
import structures.statemachine.EndTurnState;
import structures.statemachine.GameStateMachine;
//import structures.statemachine.HumanAttackState;
import structures.statemachine.NoSelectionState;
import structures.statemachine.State;
import structures.statemachine.UnitMovingState;
import utils.Constants;

class TurnCache {
    public List<Unit> markedUnits = new ArrayList<>();
    public List<Unit> aiUnits = new ArrayList<>();
    public List<Unit> playerUnits = new ArrayList<>();

    public TurnCache() {

    }

    public TurnCache(GameState gameState) {
        this.playerUnits = searchTargets(gameState);
        this.aiUnits =getAvailableUnits(gameState);
    }

    public List<Unit> searchTargets(GameState gameState) {
        List<Unit> units = gameState.board.getUnits().stream().filter((unit -> !unit.isAi())).collect(Collectors.toList());
        return units;
    }

    public List<Unit> getAvailableUnits(GameState gameState) {
        List<Unit> units = gameState.board.getUnits();
        units = units.stream().filter((unit -> {
            return unit.isAi() && (unit.canAttack() || unit.getMovement());
        })).collect(Collectors.toList());
        return units;
    }
}

/**
 * AIPlayer implements an AI which can analyse the positions on the board, calculate different actions,
 * and execute the actions one by one over the board. The actions it can perform includes moving a unit from one tile to another,
 * selecting a unit and then using it to attack an enemy unit, cast a card from its hand, etc.
 */
public class AIPlayer{

    private boolean canPlay = true;

    private State nextAiMove = null;
    
 	 List<Card> cards;

    private TurnCache turnCache = null;

    private List<AiAction> aiActions = new ArrayList<>();

    public AIPlayer() {
        this.nextAiMove = null;
        turnCache = new TurnCache();
    }

    public void beginTurn(GameState gameState) {
        turnCache = new TurnCache(gameState);
    }

    /**
     * The search action method will analyze the AI's position on the board and then find appropriate actions it should perform.
     * The actions it can perform will be stored in the class variable 'actions'.
     */
   // public boolean searchAction(ActorRef out,GameState gameState, GameStateMachine gameStateMachine) {
        //Create a list of actions to be performed
        //Check if deck of cards has unit card
//<<<<<<< HEAD
//        if(canPlay) {
//            Unit unit = gameState.aiUnit;
//            gameState.targetTile=gameState.board.getTile(unit.getPosition().getTilex() - 3, unit.getPosition().getTiley());
//            //get a card
//            
//            if(null!=gameState.targetTile.getUnit())
//            {
//            	aiCastCard(out, gameState, gameStateMachine);
//            	State unitMove = new UnitMovingState(unit, gameState.board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()), gameState.board.getTile(unit.getPosition().getTilex() - 2, unit.getPosition().getTiley()));
//            	State aiAttack = new HumanAttackState(unit, gameState.targetTile, false,false);
//            	unitMove.setNextState(aiAttack);
//            	nextAiMove.setNextState(unitMove);
//            	canPlay = false;
//            }
//            else
//            {
//            	nextAiMove = new UnitMovingState(unit, gameState.board.getTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()), gameState.board.getTile(unit.getPosition().getTilex() +1, unit.getPosition().getTiley()));
//            	canPlay = false;
//            }
//         
//            //Move unit towards enemies
//           // canPlay = false;
//            return false;
//        } else {
//            canPlay = true;
//            return true;
//=======
    public boolean searchAction(GameState gameState) {
    	   //get the all the ai hand card
            cards = gameState.board.getCards();
            nextAiMove = null;
            turnCache.aiUnits = turnCache.getAvailableUnits(gameState);
            markEnemy();
            pursueEnemy();
            for(AiAction action : aiActions) {
                State s  = action.processAction(gameState);
                if(s == null) continue;
                if(nextAiMove == null) {
                    nextAiMove = s;
                } else {
                    nextAiMove.appendState(s);
                }
            }
            canPlay = !aiActions.isEmpty();
            aiActions.clear();
            return canPlay;
    }

    public void markEnemy() {
        turnCache.markedUnits = turnCache.playerUnits;
    }

    public void pursueEnemy() {
        if(turnCache.aiUnits.isEmpty())
            return;
        for(Unit markedUnit : turnCache.markedUnits) {
             turnCache.aiUnits.sort(Comparator.comparingInt(a -> a.getDistance(markedUnit)));
             turnCache.aiUnits.stream()
                     .filter(aiUnit -> {return (aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) || aiUnit.getMovement();})
                     .findFirst()
                     .ifPresent((aiUnit -> {
                         AiAction pursueAction = new PursueAction(markedUnit, aiUnit);
                         aiActions.add(pursueAction);
                         turnCache.aiUnits.remove(aiUnit);
                     }));

//             turnCache.aiUnits.removeIf(aiUnit -> {
//                 boolean hit = (aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) || aiUnit.getMovement();
//                 if(!hit) return false;
//                 Action pursueAction = new PursueAction(markedUnit, aiUnit);
//                 aiActions.add(pursueAction);
//                 return true;
//             });
        }
    }
    
    private void aiCastCard(ActorRef out, GameState gameState, GameStateMachine gameStateMachine)
    {
    	//check the card on hand
    	chooseAiCardPosition(gameState);
    	Tile chooseTile=Action.searchLowestAiUnitAttack(out, gameState);
    	System.out.println("Ai cast the card");
    	nextAiMove = new CardSelectedState(out, 1, chooseTile, gameState);
           
    }
    
    
    private int chooseAiCardPosition(GameState gameState)
    {
  
    	 for(int i=1;i<=cards.size();i++)
    	 {
    		Card aiCard=cards.get(i-1);
    	 	if(null!=aiCard&&gameState.AiPlayer.getMana()>=aiCard.getManacost())
    	 	{
    	 		return i;
    	 	}
    	 }
    	return 0;
    }

    public State getNextAiMove() {
        return nextAiMove;
    }
}
