# MSc Software Development Team Project

## Overview
This team project was developed as part of the COMPSCI5074 MSc IT+ Masters Team Project at University of Glasgow in a group of five students. The goal of the project was to develop a tactical card game utilizing card-game mechanics and chess-like board. During the project the backend of the game was implemented.  

## Getting started

Clone the repo to your local machine and use the following commands to build and run the application
```
cd IT-SD-Team-Project
./sbt compile
./sbt run
```
After the server starts, the game can be played from any web browser by accessing it on the url ```localhost:9000/game```. 
## Features Implemented
### Moving and Attacking
The player can move the units on the board and order it to attack enemy units.

<img src="https://github.com/NipunRamani99/IT-SD-Team-Project/blob/main/media/player_move_attack.gif?raw=true" width="60%" height="60%" />
### Units and Spells
The player can use a variety of cards which can be used deploy units or cast spells with special abilities.
<img src="https://github.com/NipunRamani99/IT-SD-Team-Project/blob/main/media/unit_spell_use.gif?raw=true" width="60%" height="60%" />


### AI
The AI can order its unit to move around the board, attack player units and use the cards it has in it's deck.
<img src="https://github.com/NipunRamani99/IT-SD-Team-Project/blob/main/media/ai_behavior.gif?raw=true" width="60%" height="60%" />

## Implementation Overview
This project makes use of the Finite State Machine pattern which is driven by the various events in the ```app/events``` folder.
The GameStateMachine is a key component in capturing all the game rules in an elegant manner. Otherwise the rules of the game had to be implemented in lengthy if-else chains.


```java
/**
 * GameStateMachine provides encapsulates the current state of the game and provides method to switch between states.
 */
public class GameStateMachine {
    private State currentState;
    public GameStateMachine() {
        currentState = new NoSelectionState();
    }

    public void setState(State newState, ActorRef out, GameState gameState) {
    	try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    	if(gameState.AiPlayer.getHealth()==0||gameState.humanPlayer.getHealth()==0)
    		newState= new NoSelectionState();
        newState.enter(out, gameState);
        currentState = newState;
    }

    public void processInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor eventProcessor) {
        try {
			currentState.handleInput(out, gameState, message, eventProcessor,this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    public State getCurrState()
    {
    	return currentState;
    }
}
``` 

The rules of the games are implemented using the State class and it's child classes. 

```java 
/**
 * State base class which will be extended into specific game states.
 */
public abstract class State {

    protected State nextState  = null;

    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) throws Exception {}

    public void enter(ActorRef out, GameState gameState) {}

    public void setNextState(State nextState) {
        this.nextState = nextState;
    }

    public State getNextState() { return nextState;}
    public void appendState(State s) {
    	if(s==null) return;
        if(nextState == null) {
            nextState=s;
        }
        else {
            nextState.appendState(s);
        }
    }
}

```

To give an example of how the State class is used, when the player clicks on a unit, the state of the GameStateMachine will transition from NoSelectionState to UnitMovingState which will highlight the tiles of the board according to the abilities of the unit selected.



## Outcome
At the end of the project, all the core features and story cards were implemented. The game contains few bugs which were not resolved due to time constraint, however the overall experience and feedback on the project has been positive.
