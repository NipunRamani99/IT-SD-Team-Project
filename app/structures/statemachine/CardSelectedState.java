package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.Heartbeat;
import events.OtherClicked;
import events.TileClicked;
import structures.GameState;
import structures.Turn;
import structures.basic.*;

import utils.BasicObjectBuilders;
import utils.Constants;
import utils.StaticConfFiles;

import java.util.List;

enum CardType {
    UNIT,
    SPELL
}

public class CardSelectedState extends State{
    private int handPosition = 0;
    private Card cardSelected = null;
    private Tile aiTile =null;
    private CardType cardType;

    public CardSelectedState(ActorRef out, JsonNode message, GameState gameState) {
        gameState.resetCardSelection(out);
        handPosition = message.get("position").asInt();
        cardSelected=gameState.board.getCard(handPosition);
        BasicCommands.drawCard(out, cardSelected, handPosition, 1);

        if(cardSelected.getBigCard().getHealth() < 0) {
            cardType = CardType.SPELL;
        } else {
            cardType = CardType.UNIT;
        }
        cardClickedTilesHighlight(out, gameState);
    }
    
    public CardSelectedState(ActorRef out,int position, Tile tile, GameState gameState)
    {
    	cardSelected=gameState.board.AIgetCard(position);
    	aiTile=tile;
    	handPosition= position;
    	if(cardSelected.getBigCard().getHealth() < 0) {
            cardType = CardType.SPELL;
        } else {
            cardType = CardType.UNIT;
        }
    	
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
        if(event instanceof TileClicked) {
            int tilex = gameState.position.getTilex();
            int tiley = gameState.position.getTiley();
            Tile tile = gameState.board.getTile(tilex, tiley);
            System.out.println(String.format("tiles:%d,%d",tilex,tiley));
            System.out.println("TileSelectedState: Tile Clicked");
            if(tile.getTileState() == TileState.None) {
                gameState.resetBoardSelection(out);
                gameState.resetCardSelection(out);
                System.out.println("CardSelectedState: None Tile Clicked");
                gameStateMachine.setState(new NoSelectionState());
            } else if (tile.getTileState() == TileState.Reachable || tile.getTileState() == TileState.Occupied) {
                //if the tile is reachable and card is a unit card
            	int mana=gameState.humanPlayer.getMana();
            	if(mana>=cardSelected.getManacost())
            	{
            		 if(tile.getTileState() == TileState.Reachable && CastCard.isUnitCard(cardSelected)) {
                     	gameState.humanPlayer.setMana(mana-cardSelected.getManacost());
                        	BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
                         try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                         //Cast card
                         CastCard.castUnitCard(out, cardSelected, tile, gameState);
                         //Delete card
                         BasicCommands.deleteCard(out, handPosition);
                         try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                         gameState.board.deleteCard(handPosition);
                         System.out.println("CardSelectedState: Reachable Tile Clicked");                    
                     //if the tile is occupied and card is a spell card(spell needs unit to use)
                     }else if(tile.getTileState() == TileState.Occupied && !CastCard.isUnitCard(cardSelected)) {
                     	gameState.humanPlayer.setMana(mana-cardSelected.getManacost());
                        	BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
                         try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                     	//Cast card
                         CastCard.castSpellCard(out, cardSelected, tile, gameState);
                         //Delete card
                         BasicCommands.deleteCard(out, handPosition);  
                         try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                         gameState.board.deleteCard(handPosition);             	
                         System.out.println("CardSelectedState: Occupied Tile Clicked");
                     }
            	}
               
                gameState.resetBoardSelection(out);
                gameState.resetCardSelection(out);
                gameStateMachine.setState(new NoSelectionState());
            }
        } else if(event instanceof CardClicked) {
            gameState.resetBoardSelection(out);
            System.out.println("CardSelectedState: Card Clicked");
            cardClickedTilesHighlight(out, gameState);
            gameStateMachine.setState(new CardSelectedState(out, message, gameState));
        }else if(event instanceof OtherClicked)
        {
        	  gameState.resetBoardSelection(out);
        	  gameState.resetCardSelection(out);
        	  gameStateMachine.setState(new NoSelectionState());
        }
        else if(gameState.currentTurn==Turn.AI) 
        {
        	int mana=gameState.AiPlayer.getMana();
        	if(mana>=cardSelected.getManacost())
        	{
        		//Cast unit
            	if(cardType==CardType.UNIT)
            	{
            		
            		 CastCard.castUnitCard(out, cardSelected, aiTile, gameState);
            		 //cost the ai mana
            		 gameState.AiPlayer.setMana( mana-cardSelected.getManacost());
            		 BasicCommands.setPlayer2Mana(out, gameState.AiPlayer);
            		 try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                     //Delete card
                     BasicCommands.deleteCard(out, handPosition);
                     gameState.board.deleteAiCard(handPosition);
            	}
            	else  //cast spell
            	{
            		CastCard.castSpellCard(out, cardSelected, aiTile, gameState);
              		 //cost the ai mana
           		    gameState.AiPlayer.setMana( mana-cardSelected.getManacost());
           		    BasicCommands.setPlayer2Mana(out, gameState.AiPlayer);
           		    try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
            		 //Delete card
                    BasicCommands.deleteCard(out, handPosition);
                    gameState.board.deleteAiCard(handPosition);
            	}
        	}
        	else
        	{
        		nextState= new NoSelectionState();
        	}
       	    
        	gameState.resetBoardSelection(out);
        	gameStateMachine.setState(nextState,out, gameState);
           
        } 
        else {
            System.out.println("CardSelectedState: Invalid Event");
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
        BasicCommands.drawCard(out, cardSelected, handPosition, 1);
        cardClickedTilesHighlight(out, gameState);   
          
    }
    
    /**
     * High the tiles when click the card
     * @param out
     * @param gameState
     */
    private void cardClickedTilesHighlight(ActorRef out, GameState gameState)
    {
	      if(cardSelected.getBigCard().getHealth() < 0) {
	            cardType = CardType.SPELL;
	      } else {
	            cardType = CardType.UNIT;
	      }
    	
    	  if(gameState.humanMana>=cardSelected.getManacost())
          {
	       	  if(cardType == CardType.UNIT)
	       	  {
	       		  highlightUnitCardSelection(out, gameState);
	       	  }
                
              else if(cardType == CardType.SPELL)
              {
	       		  //highlight enemy unit
	       		  if(cardSelected.getCardname().equals("Truestrike"))
	       		  {
	       			  highlightEnemyUnitSelection(out, gameState);
	       		  }
	       		  //highlight non avatar units
	       		  else if(cardSelected.getCardname().equals("Entropic Decay"))
	       		  {
	       			  highlightNoAvatarSelection(out, gameState);
	       		  }
	       		  //highlight all the units
	       		  else
	       		  {
	       			  highlightSpellCardSelection(out, gameState);
	       		  }
               }
          }
    }

    @Override
    public void exit(ActorRef out, GameState gameState) {

    }

    /**
     * Highlight tiles for the general Unit card
     * @param out
     * @param gameState
     */
    private void highlightUnitCardSelection(ActorRef out, GameState gameState)
    {
        BasicCommands.addPlayer1Notification(out, "Card highlight ",1);
        List<Unit> unitList = gameState.board.getUnits();
        
        for(Unit unit : unitList) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if(i == 0 && j == 0) continue;;
                    int x = unit.getPosition().getTilex() + i;
                    int y = unit.getPosition().getTiley() + j;
                    Tile surroundingTile = gameState.board.getTile(x, y);
                    if (surroundingTile != null && !unit.isAi()) {
                        if (surroundingTile.getUnit() == null&&surroundingTile.getAiUnit()==null) {
                            surroundingTile.setTileState(TileState.Reachable);
                            BasicCommands.drawTile(out, surroundingTile, 1);
                            try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                        }
                    }
                }
            }
        }
    }

    /**
     * Highlight the tiles for the general spell card 
     * @param out
     * @param gameState
     */
    private void highlightSpellCardSelection(ActorRef out, GameState gameState)
    {
        BasicCommands.addPlayer1Notification(out, "Card highlight ",1);
        List<Unit> unitList = gameState.board.getUnits();
        for(Unit unit : unitList) {
            Position tilePos = unit.getPosition();
            Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
            BasicCommands.drawTile(out, tile, 2);
            tile.setTileState(TileState.Occupied);
        }
    }
    
    /**
     * High light the enemy unit tiles selection 
     * @param out
     * @param gameState
     */
    private void highlightEnemyUnitSelection(ActorRef out, GameState gameState)
    {
    	  BasicCommands.addPlayer1Notification(out, "enemy unit highlight ",1);
    	  List<Unit> unitList = gameState.board.getUnits();
          for(Unit unit : unitList) {
              if(unit.isAi())
              {
            	  Position tilePos = unit.getPosition();
                  Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
                  BasicCommands.drawTile(out, tile, 2);
                  tile.setTileState(TileState.Occupied);
              }
          }
        	 
    }
    
    
    /**
     * High light the unit tiles selection
     * @param out
     * @param gameState
     */
    private void highlightUnitSelection(ActorRef out, GameState gameState)
    {
    	  BasicCommands.addPlayer1Notification(out, "Unit highlight ",1);
    	  List<Unit> unitList = gameState.board.getUnits();
          for(Unit unit : unitList) {
              if(!unit.isAi())
              {
            	  Position tilePos = unit.getPosition();
                  Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
                  BasicCommands.drawTile(out, tile, 2);
                  tile.setTileState(TileState.Occupied);
              }
          }
        	 
    }
    
    /**
     * High light the avatar tile selection
     * @param out
     * @param gameState
     */
    private void highlightAvatarSelection(ActorRef out, GameState gameState)
    {
    	  BasicCommands.addPlayer1Notification(out, "Avatar highlight ",1);
    	  List<Unit> unitList = gameState.board.getUnits();
          for(Unit unit : unitList) {
        	  if(gameState.currentTurn==Turn.AI)
        	  {
        		  if(unit.isAvatar()&&unit.isAi())
                  {
                	  Position tilePos = unit.getPosition();
                      Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
                      BasicCommands.drawTile(out, tile, 2);
                      tile.setTileState(TileState.Occupied);
                  } 
        	  }
        	  else
        	  {
        		  if(unit.isAvatar()&&!unit.isAi())
                  {
                	  Position tilePos = unit.getPosition();
                      Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
                      BasicCommands.drawTile(out, tile, 2);
                      tile.setTileState(TileState.Occupied);
                  } 
        	  }
             
          }
        	 
    }
    
    /**
     * High light other units
     * @param out
     * @param gameState
     */
    private void highlightNoAvatarSelection(ActorRef out, GameState gameState)
    {
    	  BasicCommands.addPlayer1Notification(out, "No avatar highlight ",1);
    	  List<Unit> unitList = gameState.board.getUnits();
          for(Unit unit : unitList) {
              if(!unit.isAvatar())
              {
            	  Position tilePos = unit.getPosition();
                  Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
                  BasicCommands.drawTile(out, tile, 2);
                  tile.setTileState(TileState.Reachable);
              }
          }
        	 
    }
    
    /**
     * High light available tiles
     * @param out
     * @param gameState
     */
    private void highlightAvailableSelection(ActorRef out, GameState gameState)
    {
    	  BasicCommands.addPlayer1Notification(out, "Available tile highlight ",1);
    	  
    	  for(int i=0;i<Constants.BOARD_WIDTH;i++)
    	  {
    		  for(int j=0;j<Constants.BOARD_HEIGHT;j++)
    		  {
    			  Tile tile= gameState.board.getTile(i, j);
    			  if(null==tile.getAiUnit()&& null==tile.getUnit())
    			  {
    				  BasicCommands.drawTile(out, tile, 2);
    				  tile.setTileState(TileState.Reachable);
    			  }
    		  }
    	  }
        	 
    }

}
