package ai;
/**
 * This enum represents the set of actions the AI can perform to interact with the game board.
 */
public enum ActionType {
    /**
     * No action
     */
    None,
    /**
     * Moving the unit
     */
    UnitMove,
    /**
     * unit launch an attack
     */
    UnitAttack,
    /**
     * Moving the avatar
     */
    AvatarMove,
    /**
     * Avatar launch an attack
     */
    AvatarAttack,
    /**
     * The action is to cast a unit on the tile from card in the hand
     */
    CastUnitCard,
    /**
     * The action is to cast a spell on the unit on the board
     */
    CastSpellCard,
    /**
     * The action is performed by the player to end the turn
     */
    EndTurn
}
