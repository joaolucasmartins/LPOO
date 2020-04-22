package Controller.Strategy;

import room.Position;
import room.Room;
import room.element.Element;
import room.element.Entity;
import room.element.MoveStrategy;
import room.element.skane.Scent;
import room.element.skane.Skane;
import room.element.skane.SkaneBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MeleeMoveStrat implements MoveStrategy {
    private static class PosDist {
        public Position p;
        public double dist;

        public PosDist(Position p, double dist) {
            this.p = p;
            this.dist = dist;
        }
    }

    private List<Position> convertToSortPosList(List<PosDist> pdList, Position p) {
        pdList.sort(Comparator.comparingDouble(lhs -> lhs.dist));

        double a, b, c;
        List<Position> posList = new ArrayList<>();
        for (PosDist pd : pdList) {
            a = pd.p.getX() - p.getX();
            b = pd.p.getY() - p.getY();
            c = Math.sqrt(a * a + b * b);
            a /= c;
            b /= c;

            // TODO check if second position puts u further away from target
            if (Math.abs(a) > Math.abs(b)) {
                posList.add(new Position(p.getX() + (a > 0 ? 1 : -1), p.getY()));
                posList.add(new Position(p.getX(), p.getY() + (b > 0 ? 1 : -1)));
            } else {
                posList.add(new Position(p.getX(), p.getY() + (b > 0 ? 1 : -1)));
                posList.add(new Position(p.getX() + (a > 0 ? 1 : -1), p.getY()));
            }
        }

        return posList;
    }

    private void addRayPos(Room room, List<PosDist> posDistList, Position s, Position t) {
        List<Element> rayResult = room.raycast(s, t);
        if (rayResult.size() > 0) {
            if (room.isSkanePos(rayResult.get(0).getPos()))
                posDistList.add(new PosDist(t, s.dist(t)));
        }
    }

    private boolean checkRayScent(Room room, Position sourcePos, Position scentPos) {
        List<Element> rayResult = room.raycast(sourcePos, scentPos);

        return rayResult.size() == 0;
    }

    private int i = 0;

    @Override
    public List<Position> execute(Room room, Entity e) {
        ++i;
        if (i < 5) { // TODO
            return new ArrayList<>();
        }
        i = 0;
        /*
         * Attempts to get to the first part of the skane it sees
         * or follow some scent.
         */
        if (room.isSkaneBury())
            return new ArrayList<>();

        Position ePos = e.getPos();
        Skane ska = room.getSkane();
        List<PosDist> listPos = new ArrayList<>();

        // Head
        addRayPos(room, listPos, ePos, ska.getPos());

        // Body
        for (SkaneBody sb : ska.getBody())
            addRayPos(room, listPos, ePos, sb.getPos());

        // Scent
        if (listPos.size() == 0) { // If can't see skane
            Scent freshestScent = null;
            for (Scent s : ska.getScentTrail()) {
                if (checkRayScent(room, ePos, s.getPos()))
                    freshestScent = s;
            }

            if (freshestScent != null)
                listPos.add(new PosDist(freshestScent.getPos(), ePos.dist(freshestScent.getPos())));
        }

        return convertToSortPosList(listPos, ePos);
    }
}