package ai;


import structures.GameState;
import structures.basic.Tile;
import utils.Constants;

/**
 * AIActionUtils class is used to implement the different actions an AI can perform while playing the game.
 * Each action is defined by an action type, instances of tiles on the board,
 * and a hand position if the action involves casting a card from the AI's hand.
 */
public class AIActionUtils {

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
   
   //Search for the non-avatar unit highest health point
   public static Tile searchHighestNonAvatarUnitHealth(GameState gameState)
   {
	   Tile selectedTile=null;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               {
            	   if(null!=surroundingTile.getUnit())
            		   if( surroundingTile.getUnit().getName().contains("Ironcliff Guardian"))
            			   selectedTile=surroundingTile;
               }
           }
       }
       
	   return selectedTile;
   }

   //Search for the AI avatar unit highest health point
   public static Tile searchAiAvatarTile(GameState gameState)
   {
	   Tile selectedTile=null;
       for (int i =0; i <Constants.BOARD_WIDTH; i++) {
           for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
               Tile surroundingTile = gameState.board.getTile(i, j);
               {
            	   if(null!=surroundingTile.getUnit())
            		   if( surroundingTile.getUnit().getId()==1)
            			   selectedTile=surroundingTile;
               }
           }
       }
       
	   return selectedTile;
   }

  
    
 
    
    
}
