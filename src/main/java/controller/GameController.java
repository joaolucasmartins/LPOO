package controller;

import com.googlecode.lanterna.TerminalSize;
import creator.RoomCreator;
import room.Position;
import gui.Gui;
import gui.EVENT;
import room.Room;

import java.io.IOException;

public class GameController {
    private Room room;
    private Gui gui;
    private GAMEST state;
    private EnemyController enemyController;
    private SkaneController skaneController;
    private CollisionHandler colHandler;
    private final int DELAY = 30; // time between frames (in ms)

    public static void main(String[] args) throws IOException {
        new GameController().start();
    }

    private void handleEvent(EVENT event) {
        if (event == EVENT.NullEvent) return;
        else if (event == EVENT.QuitGame) this.end();
        else if (event == EVENT.RestartGame) this.state = GAMEST.RESTART;
        else if (event == EVENT.Bury) skaneController.toggleBury();
        else { // Movement Event
            Position new_pos = room.getSkane().getPos();
            switch (event) {
                case MoveLeft:
                    new_pos = room.getSkane().moveLeft();
                    break;
                case MoveRight:
                    new_pos = room.getSkane().moveRight();
                    break;
                case MoveUp:
                    new_pos = room.getSkane().moveUp();
                    break;
                case MoveDown:
                    new_pos = room.getSkane().moveDown();
                    break;
            }
            skaneController.tickScentTrail(); // TODO
            if (colHandler.canSkaneMove(new_pos))
                room.moveSkane(new_pos);
        }
    }

    public GameController(Room room, Gui gui, SkaneController skactr) {
        this.room = room;
        this.gui = gui;
        this.state = GAMEST.RUNNING;
        this.colHandler = new CollisionHandler(this.room);
        this.enemyController = new EnemyController(this.room, this.colHandler);
        this.skaneController = skactr;
    }

    public GameController(Room room) throws IOException {
        this(room, new Gui(room),
                new SkaneController(room.getSkane(), 50));
    }

    public GameController() throws IOException {
        this(new RoomCreator().createRoom(80, 40));
    }

    private void run() throws IOException {
        long beforeTime, timeDiff;
        beforeTime = System.currentTimeMillis();

        gui.startInputHandler();
        while (state == GAMEST.RUNNING) {
            gui.draw();
            handleEvent(gui.getEvent());
            gui.releaseKeys();

            skaneController.inhale();
            enemyController.MoveEnemies();

            timeDiff = System.currentTimeMillis() - beforeTime;
            try {
                if (DELAY - timeDiff > 0)
                    Thread.sleep(DELAY - timeDiff);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: %s" + e.getMessage());
            }
            beforeTime = System.currentTimeMillis();
        }
    }

    public void start() throws IOException {
        this.state = GAMEST.RUNNING;

        while (this.state != GAMEST.STOPPED) {
            this.run();
            if (this.state == GAMEST.RESTART)
                this.restart();
        }

        gui.close();
    }

    public void restart() {
        this.state = GAMEST.RUNNING;
        TerminalSize ts = gui.getTermSize();
        this.room = new RoomCreator().createRoom(ts.getColumns(), ts.getRows());

        //this.gui.stopInputHandler();
        this.gui.setRoom(this.room);
        this.colHandler = new CollisionHandler(this.room);
        this.skaneController = new SkaneController(room.getSkane(), 50);
        this.enemyController = new EnemyController(this.room, this.colHandler);
    }

    public void end() {
        this.state = GAMEST.STOPPED;
    }

    public Room getRoom() {
        return room;
    }
}