package org.g73.skanedweller.model.elements;

import org.g73.skanedweller.model.element.Laser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LaserTests {
    private Laser laser;

    @BeforeEach
    public void setUp() {
        this.laser = new Laser(10, 10, 2, 2, 0);
    }

    @Test
    public void testLaserInit() {
        assertFalse(laser.getReadiness());
        assertEquals(laser.getDuration(), 2);
    }

    @Test
    public void testLaserReadiness() {
        laser.makeUnready();
        assertFalse(laser.getReadiness());

        laser.makeReady();
        assertTrue(laser.getReadiness());
        laser.makeReady();
        assertTrue(laser.getReadiness());

        laser.makeUnready();
        assertFalse(laser.getReadiness());
    }

    @Test
    public void testLaserDuration() {
        assertEquals(laser.getDuration(), 2);
        laser.tickLaser();
        assertEquals(laser.getDuration(), 1);
        laser.tickLaser();
        assertEquals(laser.getDuration(), 0);
        laser.tickLaser();
        assertEquals(laser.getDuration(), -1);
    }
}
