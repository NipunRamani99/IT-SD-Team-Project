package ai;


import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Tile.Occupied;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.Constants;
import utils.StaticConfFiles;
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
    
    /**
     * The board
     */
    private Board board;
    
    /**
     * The AI player
     */
    private Player playerAI;
    
    
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
     *  The actor Reference
     */
    private ActorRef out;
    
    /**
     * Game state
     */
    private GameState gameState;

    
  //Search for the unit lowest health
   public static Tile searchLowestUnitHealth(GameState gameState)
   {
	   Tile selectedTile=null;
	   int health=20;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               if (null!=surroundingTile.getUnit()&&!surroundingTile.getUnit().isAi()) {
                   int unitHealth= surroundingTile.getUnit().getHealth();
                   if(unitHealth<=health)
                   {
                	   selectedTile=surroundingTile;
                   }
               }
           }
       }
       
	   return selectedTile;
   }
   
   //Search for the Ai unit lowest health
   public static Tile searchLowestAiUnitHealth(GameState gameState)
   {
	   Tile selectedTile=null;
	   int health=20;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               if (null!=surroundingTile.getUnit()&&surroundingTile.getUnit().isAi()) {
                   int unitHealth= surroundingTile.getUnit().getHealth();
                   if(unitHealth<=health)
                   {
                	   selectedTile=surroundingTile;
                   }
               }
           }
       }
       
	   return selectedTile;
   }
    
   //Search for the Ai unit lowest attack point
   public static Tile searchLowestAiUnitAttack(GameState gameState)
   {
	   Tile selectedTile=null;
	   int attack=20;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               if (null!=surroundingTile.getUnit()&&surroundingTile.getUnit().isAi()) {
                   int unitAttack= surroundingTile.getUnit().getAttack();
                   if(unitAttack<=attack)
                   {
                	   selectedTile=surroundingTile;
                   }
               }
           }
       }
       
	   return selectedTile;
   }

   
   //Search for the Ai unit highest attack point
   public static Tile searchHighestAiUnitAttack(GameState gameState)
   {
	   Tile selectedTile=null;
	   int attack=0;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               if (null!=surroundingTile.getUnit()&&surroundingTile.getUnit().isAi()) {
                   int unitAttack= surroundingTile.getUnit().getAttack();
                   if(unitAttack>=attack)
                   {
                	   selectedTile=surroundingTile;
                   }
               }
           }
       }
       
	   return selectedTile;
   }
   
   //Search for the unit highest health point
   public static Tile searchHighestUnitHealth(GameState gameState)
   {
	   Tile selectedTile=null;
	   int health=0;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               if (null!=surroundingTile.getUnit()&&!surroundingTile.getUnit().isAi()) {
                   int unitHealth= surroundingTile.getUnit().getHealth();
                   if(unitHealth>=health)
                   {
                	   selectedTile=surroundingTile;
                   }
               }
           }
       }
       
	   return selectedTile;
   }
   
   //Search for the non-avator unit highest health point
   public static Tile searchHighestNonAvatarAllUnitHealth(GameState gameState)
   {
	   Tile selectedTile=null;
	   int health=0;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               if (null!=surroundingTile.getUnit()&&!surroundingTile.getUnit().isAvatar()) {
                   int unitHealth= surroundingTile.getUnit().getHealth();
                   if(unitHealth>=health)
                   {
                	   selectedTile=surroundingTile;
                   }
               }
           }
       }
       
	   return selectedTile;
   }
   
   //Search for the non-avator unit highest health point
   public static Tile searchHighestNonAvatarUnitHealth(GameState gameState)
   {
	   Tile selectedTile=null;
	   int health=0;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               if (null!=surroundingTile.getUnit()&&!surroundingTile.getUnit().isAvatar()
            		   &&!surroundingTile.getUnit().isAi()) {
                   int unitHealth= surroundingTile.getUnit().getHealth();
                   if(unitHealth>=health)
                   {
                	   selectedTile=surroundingTile;
                   }
               }
           }
       }
       
	   return selectedTile;
   }


  
    
 
    
    
}
