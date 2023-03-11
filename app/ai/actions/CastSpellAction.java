package ai.actions;

import java.util.List;

import ai.Action;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.statemachine.CardSelectedState;
import structures.statemachine.CastCard;
import structures.statemachine.State;

public class CastSpellAction implements AiAction{
	
	private ActorRef out;
	private  List<Card> cards;
    private Unit markedUnit = null;
    private Unit aiUnit = null;
    
    private Tile tile=null;
	
	public CastSpellAction(ActorRef out,Unit AiUnit, Unit enemyUnit)
	{
		this.out=out;
		this.aiUnit=AiUnit;
		this.markedUnit=enemyUnit;
	}
	
	public CastSpellAction(ActorRef out)
	{
		this.out=out;
	}
	
	private void getSpellTile(Card card,GameState gameState)
	{
		switch(card.getCardname())
		{
			case "Staff of Y'Kir'":
				tile= Action.searchLowestAiUnitAttack(out, gameState);		
				break;
			case "Entropic Decay":
				 tile =Action.searchHighestNonAvatarUnitHealth(out, gameState);
				 break;		
		}
			
	}
	
    @Override
    public State processAction(GameState gameState) {
    	cards=gameState.board.getAiCards();
    	Card card=null;
    	for(Card c:cards)
    	{
    		//only works on the spell card
    		if(c.getBigCard()!=null)
    		{
    			if(gameState.AiPlayer.getMana()>=c.getManacost()
    		       &&c.getBigCard().getHealth()<0)
	    		{
    				card=c;
    				getSpellTile(c,gameState);
	    		}
    		}
    		else
    		{
    			break;
    		}
    		
    	}
    	if(tile!=null&&card!=null)
    		return new CardSelectedState(out,card.getCardPosition(), tile, gameState);
    	else
    		return null;
    }
}
