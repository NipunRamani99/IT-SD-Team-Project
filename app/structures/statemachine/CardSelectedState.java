package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import structures.basic.*;

import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.List;

enum CardType {
    UNIT,
    SPELL
}

public class CardSelectedState extends State{
    private int handPosition = 0;
    private Card cardSelected = null;

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
        if(gameState.humanMana>=cardSelected.getManacost())
            if(cardType == CardType.UNIT)
        	    highlightUnitCardSelection(out, gameState);
            else if(cardType == CardType.SPELL)
                highlightSpellCardSelection(out, gameState);

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
                if(tile.getTileState() == TileState.Reachable && CastCard.isUnitCard(cardSelected)) {
                    //Cast card
                    CastCard.castUnitCard(out, cardSelected, tile, gameState);
                    //Delete card
                    BasicCommands.deleteCard(out, handPosition);
                    gameState.board.deleteCard(handPosition);
                    System.out.println("CardSelectedState: Reachable Tile Clicked");                    
                //if the tile is occupied and card is a spell card(spell needs unit to use)
                }else if(tile.getTileState() == TileState.Occupied && !CastCard.isUnitCard(cardSelected)) {
                	//Cast card
                	//use a boolean to check if the spell is cast on a correct unit(user or ai)
                    boolean succeedcasting = CastCard.castSpellCard(out, cardSelected, tile, gameState);
                    //Delete card
                    if(succeedcasting) {
                    BasicCommands.deleteCard(out, handPosition);          
                    gameState.board.deleteCard(handPosition);
                    
                    gameState.humanPlayer.setMana( gameState.humanMana-cardSelected.getManacost());
                 	BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
                 	
                    System.out.println("CardSelectedState: Occupied Tile Clicked");
                    }
                }
                gameState.resetBoardSelection(out);

//                //assign the reachable tile to the gameState    
//            	drawUnitOnBoard(out, gameState,cardSelected,tile);
//               	//after the cast the unit, delete the card
//               	BasicCommands.deleteCard(out, handPosition);
//               	//Select the mana cost
//               	gameState.humanPlayer.setMana( gameState.humanMana-cardSelected.getManacost());
//               	BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
//                System.out.println("CardSelectedState: Reachable Tile Clicked");         

                gameStateMachine.setState(new NoSelectionState());
            }
        } else if(event instanceof CardClicked) {
            gameState.resetBoardSelection(out);
            System.out.println("CardSelectedState: Card Clicked");
            gameStateMachine.setState(new CardSelectedState(out, message, gameState));
        } else {
            System.out.println("CardSelectedState: Invalid Event");
        }
    }

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
                    if (surroundingTile != null) {
                        if (surroundingTile.getUnit() == null) {
                            surroundingTile.setTileState(TileState.Reachable);
                            BasicCommands.drawTile(out, surroundingTile, 1);
                            try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                        }
                    }
                }
            }
        }
    }

    private void highlightSpellCardSelection(ActorRef out, GameState gameState)
    {
        BasicCommands.addPlayer1Notification(out, "Card highlight ",1);
        List<Unit> unitList = gameState.board.getUnits();
        for(Unit unit : unitList) {
            Position tilePos = unit.getPosition();
            Tile tile = gameState.board.getTile(tilePos.getTilex(),tilePos.getTiley());
            BasicCommands.drawTile(out, tile, 2);
        }
    }
}
