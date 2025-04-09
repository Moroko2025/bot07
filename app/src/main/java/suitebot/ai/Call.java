package suitebot.ai;

import suitebot.game.Direction;

public class Call {
    static int phase = 1; // Keeps track of U/R or D/L phases
    static int count = 1; // Number of actions to return in each segment
    static int printed = 0; // How many actions have been returned in current segment

    static Direction getDirection() {
        Direction direction;

        if (phase % 2 == 1) { // UR phase
            if (printed < count) {
                System.out.print("U");
                direction = Direction.UP;
            } else {
                System.out.print("R");
                direction = Direction.RIGHT;
            }
        } else { // DL phase
            if (printed < count) {
                System.out.print("D");
                direction = Direction.DOWN;
            } else {
                System.out.print("L");
                direction = Direction.LEFT;
            }
        }

        printed++;

        if (printed == count * 2) {
            printed = 0;
            count++;
            phase++;
        }

        return direction;
    }

    //public static void main(String[] args){
    //    for (int i = 0; i < 1000; i++) {
    //        System.out.println(getDirection().toString());
    //    }
    //}
}