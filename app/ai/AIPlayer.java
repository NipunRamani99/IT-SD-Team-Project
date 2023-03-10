package ai;

import ai.actions.PursueAction;
import ai.actions.AiAction;
import ai.actions.*;
import ai.actions.UnitAttackAction;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttribute;
import structures.GameState;
import structures.basic.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import structures.statemachine.*;

class TurnCache {
    public List<Unit> markedUnits = new ArrayList<>();
    public List<Unit> aiUnits = new ArrayList<>();
    public List<Unit> playerUnits = new ArrayList<>();

    public TurnCache() {

    }

    public TurnCache(GameState gameState) {
        this.playerUnits = searchTargets(gameState);
        this.aiUnits = getAvailableUnits(gameState);
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

    public boolean searchAction(ActorRef out,GameState gameState,GameStateMachine gameStateMachine) {
    	//Firstly, AI will search and attack
		//get the all the ai hand card
	        cards = gameState.board.getAiCards();
	        nextAiMove = null;
	        turnCache.aiUnits = turnCache.getAvailableUnits(gameState);
	        aiCastCard(out, gameState, gameStateMachine);
	        markEnemy();
	        pursueEnemy();
	        gameState.AiMarkEnemy=true;
		        
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
//        Iterator<Unit> aiUnits=turnCache.aiUnits.iterator();
//    	Iterator<Unit> markedUnits =turnCache.markedUnits.iterator();
//   
//        while(aiUnits.hasNext())
//        {
//        	Unit aiUnit = aiUnits.next();
//        	
//        	while(markedUnits.hasNext())
//        	{
//        		Unit markedUnit = markedUnits.next();
//        		if(checkAvailableUnit(aiUnit,markedUnit))
//        		{
//        			AiAction action =null;
//        			if(aiUnit.canAttack()&&aiUnit.withinDistance(markedUnit)) {
//        				action = new UnitAttackAction(aiUnit,markedUnit);
//        			}
//        			else if(aiUnit.getMovement())
//        			{
//        				action = new PursueAction(markedUnit,aiUnit);
//        			}
//   
//        			aiActions.add(action);
//        			aiUnits.remove();
//        			markedUnits.remove();
//        			break;
//        			
//        		}
//        	}
//        	
//        }
        
        for(Unit markedUnit : turnCache.markedUnits) {
            turnCache.aiUnits.sort(Comparator.comparingInt(a -> a.getDistance(markedUnit)));
            turnCache.aiUnits.stream()
                     .filter(aiUnit -> {return (aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) || (aiUnit.getMovement() && !aiUnit.withinDistance(markedUnit)) ;})
                     .findFirst()
                     .ifPresent((aiUnit -> {
                         AiAction action = null;
                         if(aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) {
                             action = new UnitAttackAction(aiUnit, markedUnit);
                         } else if(aiUnit.getMovement() && !aiUnit.withinDistance(markedUnit)) {
                             action = new PursueAction(markedUnit, aiUnit);
                         }
                         aiActions.add(action);
                         turnCache.aiUnits.remove(aiUnit);
                     }));                  
        }
        
    }
    
    /**
     * Check the available unit
     * @param unit
     * @param markedUnit
     * @return boolean value
     */
//    private boolean checkAvailableUnit(Unit unit,Unit markedUnit)
//    {
//    	if((unit.canAttack()&&unit.withinDistance(markedUnit))||unit.getMovement())
//    	{
//    		return true;
//    	}
//    	else
//    	{
//    		return false;
//    	}
//    }

    /**
     * Ai cast the card from the hand
     * @param out
     * @param gameState
     * @param gameStateMachine
     */
    private void aiCastCard(ActorRef out, GameState gameState, GameStateMachine gameStateMachine)
    {
    	//check the card on hand
       AiAction castSpell = new CastSpellAction(out);
       AiAction castUnit = new CastUnitAction(out,turnCache.markedUnits);
       aiActions.add(castSpell); 
       aiActions.add(castUnit);
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
