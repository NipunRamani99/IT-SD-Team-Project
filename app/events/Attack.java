//package events;
//
//import akka.actor.ActorRef;
//import com.fasterxml.jackson.databind.JsonNode;
//import structures.GameState;
//import structures.basic.Player;
//import structures.basic.Unit;
//
//import com.fasterxml.jackson.databind.JsonNode;
//
//import akka.actor.ActorRef;
//import structures.GameState;
//import structures.basic.*;
//import structures.statemachine.GameStateMachine;
//
///**
// * This class will define the action of the unit attack, player attack, and attack between unit and player
// */
//
//public class Attack implements  EventProcessor,Runnable  {
//
//
//    /**
//     * This constructor is for the attack between two units
//     * @param myUnit the unit refers to the attacking unit
//     * @param enemyUnit the unit refer to the attacked unit
//     */
//    public void Attack(Unit myUnit, Unit enemyUnit){
//
//
//    }
//
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
//		// TODO Auto-generated method stub
//		this.out=out;
//		this.gameState=gameState;
//
//	}
//}
