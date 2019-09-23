package com.wirelessiths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.trip.Trip;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void IncrementTotalDistance() {
        Booking b = new Booking();
        Trip trip1 = new Trip();
        Trip trip2 = new Trip();
        List<Trip> trips = new ArrayList<>();
        trips.add(trip1);
        trips.add(trip2);
        trip1.setTotalDistanceMeter(2000);
        trip2.setTotalDistanceMeter(1500);
        double totalDistance = trips.stream().mapToDouble(Trip::getTotalDistanceMeter).sum();
        System.out.println(totalDistance);
        assertEquals(3500, 3.14, totalDistance);


    }
}
