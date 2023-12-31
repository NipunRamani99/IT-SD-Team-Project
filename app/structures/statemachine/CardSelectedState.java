package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;

import commands.AbilityCommands;
import commands.BasicCommands;
import events.CardClicked;
import events.EndTurnClicked;
import events.EventProcessor;
import events.OtherClicked;
import events.TileClicked;
import structures.GameState;
import structures.Turn;
import structures.basic.*;

import structures.basic.Unit;
import utils.Constants;

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

    private boolean skip = false;

    public CardSelectedState(ActorRef out, JsonNode message, GameState gameState) {
        handPosition = message.get("position").asInt();
        cardSelected=gameState.board.getCard(handPosition);
        if(cardSelected.getBigCard().getHealth() < 0) {
            cardType = CardType.SPELL;
        } else {
            cardType = CardType.UNIT;
        }
        	
    }
    
    public CardSelectedState(int position, Tile tile, GameState gameState)
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
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine){
        if(skip) {
            if(gameState.currentTurn == Turn.PLAYER)
                gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
            else
                gameStateMachine.setState(nextState != null ? nextState : new EndTurnState(), out, gameState);

        }
        if(gameState.currentTurn == Turn.PLAYER) {
            if (event instanceof TileClicked) {
                int tilex = gameState.position.getTilex();
                int tiley = gameState.position.getTiley();
                Tile tile = gameState.board.getTile(tilex, tiley);
                System.out.println(String.format("tiles:%d,%d", tilex, tiley));
                System.out.println("TileSelectedState: Tile Clicked");
                if (tile.getTileState() == TileState.None) {
                    gameState.resetBoardSelection(out);
                    gameState.resetCardSelection(out);
                    System.out.println("CardSelectedState: None Tile Clicked");
                    System.out.println("Exiting CardSelectedState");
                    gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
                } else if (tile.getTileState() == TileState.Reachable || tile.getTileState() == TileState.Occupied) {
                    //if the tile is reachable and card is a unit card
                    int mana = gameState.humanPlayer.getMana();
                    if (mana >= cardSelected.getManacost()) {
                        if (tile.getTileState() == TileState.Reachable && CastCard.isUnitCard(cardSelected)) {
                            //Cast card
                            System.out.println("CardSelectedState: Reachable Tile Clicked");
                            Unit unit;
                            try {
                                unit = CastCard.castUnitCard(out, cardSelected, tile, gameState);
                                AbilityCommands.useAbility(out, unit, gameState);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } else if (tile.getTileState() == TileState.Occupied && !CastCard.isUnitCard(cardSelected)) {
                            //Cast card
                            System.out.println("CardSelectedState: Occupied Tile Clicked");
                            try {
                                CastCard.castSpellCard(out, cardSelected, tile, gameState);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    gameState.resetBoardSelection(out);
                    gameState.resetCardSelection(out);
                    System.out.println("Exiting CardSelectedState");
                    gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
                }
            } else if (event instanceof CardClicked) {
                System.out.println("CardSelectedState: Card Clicked");
                State s = new CardSelectedState(out, message, gameState);
                s.setNextState(nextState);
                System.out.println("Exiting CardSelectedState");
                gameStateMachine.setState(s, out, gameState);
            } else if (event instanceof OtherClicked) {
                gameState.resetBoardSelection(out);
                gameState.resetCardSelection(out);
                System.out.println("Exiting CardSelectedState");
                gameStateMachine.setState(new NoSelectionState(), out, gameState);
            } else if (event instanceof EndTurnClicked) {
                gameState.resetBoardSelection(out);
                gameState.resetCardSelection(out);
                gameState.resetCardSelection(out);
                if (gameState.currentTurn == Turn.PLAYER)
                    gameStateMachine.setState(new EndTurnState(), out, gameState);
            }
        }
        else if(gameState.currentTurn==Turn.AI) 
        {
        	int mana=gameState.AiPlayer.getMana();
        	if(mana>=cardSelected.getManacost())
        	{
        		//AI cast unit
            	if(cardType==CardType.UNIT)
            	{
                    if(gameState.unitAbilityTable.getUnitAbilities(cardSelected.getCardname()).contains(UnitAbility.DRAW_CARD_ON_SUMMON)) {
                        State s = new DrawCardState(UnitAbility.DRAW_CARD_ON_SUMMON);
                        s.setNextState(nextState);
                        nextState = s;
                    }
            		 try {
						CastCard.castUnitCard(out, cardSelected, aiTile, gameState);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            	else  //AI cast spell
            	{
            		try {
						CastCard.castSpellCard(out, cardSelected, aiTile, gameState);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
        	}
      	    
        	gameState.resetBoardSelection(out);
        	gameState.resetBoardState();
            System.out.println("Exiting CardSelectedState");
            gameStateMachine.setState(nextState,out, gameState);
        } 
        else {
            System.out.println("CardSelectedState: Invalid Event");
            if(gameState.currentTurn==Turn.AI)
            {
            	gameState.resetBoardSelection(out);
            	gameState.resetBoardState();
                System.out.println("Exiting CardSelectedState");
            }
            	
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
        System.out.println("Entering CardSelectedState");
    	gameState.resetBoardSelection(out);
		gameState.resetBoardState();
        if(gameState.currentTurn==Turn.PLAYER)
            gameState.resetCardSelection(out);
        else
            gameState.resetAiCardSelection(out);

        if( aiTile != null && aiTile.getUnit()!= null) {
            skip = true;
            return;
        }

		if(gameState.currentTurn==Turn.PLAYER&&gameState.humanPlayer.getMana()>=cardSelected.getManacost())
		{
			BasicCommands.drawCard(out, cardSelected, handPosition, 1);
	        cardClickedTilesHighlight(out, gameState);
		}

		if(gameState.currentTurn==Turn.AI&&gameState.AiPlayer.getMana()>=cardSelected.getManacost())
		{
			BasicCommands.drawCard(out, cardSelected, handPosition, 1);
	        cardClickedTilesHighlight(out, gameState);
		}
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
    	
    	  if(gameState.currentTurn == Turn.PLAYER && gameState.humanMana>=cardSelected.getManacost() ||
                  gameState.currentTurn == Turn.AI && gameState.AiMana>=cardSelected.getManacost())
          {
	       	  if(cardType == CardType.UNIT)
	       	  {
	       		  if(gameState.currentTurn==Turn.PLAYER)
	       			  highlightUnitCardSelection(out, gameState);
	       		  else
	       			  aiHighlightUnitCardSelection(out, gameState);
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
	       		  } else if(cardSelected.getCardname().equals("Sundrop Elixir"))
                  {

                  }
	       		  //highlight all the units
	       		  else
	       		  {
	       			  highlightSpellCardSelection(out, gameState);
	       		  }
               }
          }
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
        if(AbilityCommands.checkSUMMON_ANYWHERE(cardSelected))
        {
            //highlight all empty tiles
            gameState.board.getTiles().forEach( tile -> {
                if(tile.getUnit() == null) {
                    tile.setTileState(TileState.Reachable);
                    BasicCommands.drawTile(out, tile, 1);
                }
            });
        }
        else {
            for (Unit unit : unitList) {
                if (!unit.isAi()) {
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (i == 0 && j == 0) continue;
                            ;
                            int x = unit.getPosition().getTilex() + i;
                            int y = unit.getPosition().getTiley() + j;
                            Tile surroundingTile = gameState.board.getTile(x, y);
                            if (surroundingTile != null) {
                                if (surroundingTile.getUnit() == null) {
                                    surroundingTile.setTileState(TileState.Reachable);
                                    BasicCommands.drawTile(out, surroundingTile, 1);
                                }
                            }
                        }
                    }
                }

            }
        }
    }
 
    /**
     * Ai highlight tiles for the general Unit card
     * @param out
     * @param gameState
     */
    private void aiHighlightUnitCardSelection(ActorRef out, GameState gameState)
    {
        BasicCommands.addPlayer1Notification(out, "Card highlight ",1);
        List<Unit> unitList = gameState.board.getUnits();
      //check the SUMMON_ANYWHERE ability
        if(!AbilityCommands.checkSUMMON_ANYWHERE(cardSelected)) {
        	for(Unit unit : unitList) {
        	if(unit.isAi())
        	{
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if(i == 0 && j == 0) continue;;
                        int x = unit.getPosition().getTilex() + i;
                        int y = unit.getPosition().getTiley() + j;
                        Tile surroundingTile = gameState.board.getTile(x, y);
                        if (surroundingTile != null) {
                            if (surroundingTile.getUnit() == null) {
                                surroundingTile.setTileState(TileState.Reachable);
                                BasicCommands.drawTile(out, surroundingTile, 1);

                            }
                        }
                    }
                }	
        	}
        }        
    }
        else {
        	for(int i=0;i<Constants.BOARD_WIDTH;i++) {
        		for(int j=0;j<Constants.BOARD_HEIGHT;j++) {   		
                    Tile surroundingTile = gameState.board.getTile(i, j);
                    if(surroundingTile.getUnit() == null) {
                    	 surroundingTile.setTileState(TileState.Reachable);
                         BasicCommands.drawTile(out, surroundingTile, 1);
                         try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
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
                  Tile tile = gameState.board.getTile(unit.getPosition());
                  if(null!=tile.getUnit())
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
                  if(null!=tile.getUnit())
                  {
                	   BasicCommands.drawTile(out, tile, 2);
                       tile.setTileState(TileState.Occupied); 
                  }             
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
                      if(null!=tile.getUnit()&&!tile.getUnit().isAi())
                      {
                    	   BasicCommands.drawTile(out, tile, 2);
                           tile.setTileState(TileState.Occupied); 
                      }
                  } 
        	  }
        	  else
        	  {
        		  if(unit.isAvatar()&&!unit.isAi())
                  {
                	  Position tilePos = unit.getPosition();
                      Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
                      if(null!=tile.getUnit()&&tile.getUnit().isAi())
                      {
                    	   BasicCommands.drawTile(out, tile, 2);
                           tile.setTileState(TileState.Occupied); 
                      }
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
    			  if( null==tile.getUnit())
    			  {
    				  BasicCommands.drawTile(out, tile, 2);
    				  tile.setTileState(TileState.Reachable);
    			  }
    		  }
    	  }
        	 
    }

}
