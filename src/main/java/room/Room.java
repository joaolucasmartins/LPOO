package room;

import room.element.Element;
import room.element.Entity;
import room.element.Skane;
import room.element.Wall;
import observe.Observable;
import observe.Observer;
import com.googlecode.lanterna.TerminalSize;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Room implements Observable<Room> {
    private int width, height;
    private List<Observer<Room>> observers;
    private Skane skane;
    private List<Wall> walls;

    public Room(int width, int height) {
        this.width = width;
        this.height = height;
        this.walls = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public Room(TerminalSize board_size) {
        this(board_size.getColumns(), board_size.getRows());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(TerminalSize new_size) {
        this.setSize(new_size.getColumns(), new_size.getRows());
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public Skane getSkane() {
        return this.skane;
    }

    public List<Element> getSamePos(Position pos) {
        List<Element> elems = new LinkedList<>();

        if (skane.getPos().equals(pos))
            elems.add(skane);

        for (Wall w : walls)
            if (w.getPos().equals(pos))
                elems.add(w);

        return elems;
    }

    public void moveSkane(Position new_p) {
        this.skane.setPos(new_p);
    }

    public void addElement(Element e) {
        if (e instanceof Skane) skane = (Skane) e;
        if (e instanceof Wall) walls.add((Wall) e);
    }

    @Override
    public void addObserver(Observer<Room> obs) {
        this.observers.add(obs);
    }

    @Override
    public void removeObserver(Observer<Room> observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(Room subject) {
        for (Observer<Room> observer : this.observers)
            observer.changed(this);
    }
}
