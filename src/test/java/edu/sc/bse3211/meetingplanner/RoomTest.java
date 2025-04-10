package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class RoomTest {
    
    private Organization org;
    private Room testRoom;
    private ArrayList<Person> attendees;
    
    @Before
    public void setUp() throws Exception {
        org = new Organization();
        testRoom = org.getRoom("LLT6A");
        attendees = new ArrayList<>();
        attendees.add(org.getEmployee("Namugga Martha"));
        
        // Create a meeting for testing
        Meeting existingMeeting = new Meeting(5, 15, 10, 12, attendees, testRoom, "Test Meeting");
        testRoom.addMeeting(existingMeeting);
    }
    
    @Test
    public void testCheckAvailabilityForUnusedRoom() throws Exception {
        Room unusedRoom = org.getRoom("LLT6B");
        assertFalse("Unused room should be available", unusedRoom.isBusy(5, 15, 10, 12));
    }
    
    @Test
    public void testCheckAvailabilityForRoomWithExistingBookings() throws Exception {
        // The room should be busy during the existing meeting time
        assertTrue("Room should be busy during exact meeting time", testRoom.isBusy(5, 15, 10, 12));
    }
    
    @Test
    public void testCheckAvailabilityForRoomDuringFreeTime() throws Exception {
        // Room should be free before and after the existing meeting
        assertFalse("Room should be free before meeting", testRoom.isBusy(5, 15, 7, 9));
        assertFalse("Room should be free after meeting", testRoom.isBusy(5, 15, 13, 15));
        
        // Room should be free on a different day
        assertFalse("Room should be free on a different day", testRoom.isBusy(5, 16, 10, 12));
    }
    
    @Test
    public void testCheckAvailabilityForRoomDuringBookedTime() throws Exception {
        assertTrue("Room should be busy during exact meeting time", testRoom.isBusy(5, 15, 10, 12));
        assertTrue("Room should be busy when start time is during meeting", testRoom.isBusy(5, 15, 11, 13));
        assertTrue("Room should be busy when end time is during meeting", testRoom.isBusy(5, 15, 9, 11));
        assertTrue("Room should be busy when meeting is fully contained in requested time by current implementation", 
                testRoom.isBusy(5, 15, 9, 13));
    }
    
    @Test(expected = Exception.class)
    public void testCheckAvailabilityForNonExistentRoom() throws Exception {
        org.getRoom("NonExistentRoom");
    }
    
    @Test(expected = TimeConflictException.class)
    public void testCheckAvailabilityForRoomWithInvalidDateParameters() throws Exception {
        // Test with invalid month
        testRoom.isBusy(13, 15, 10, 12);
    }
    
    @Test(expected = TimeConflictException.class)
    public void testCheckAvailabilityForRoomWithInvalidDayParameters() throws Exception {
        // Test with invalid day
        testRoom.isBusy(5, 32, 10, 12);
    }
    
    @Test(expected = TimeConflictException.class)
    public void testCheckAvailabilityForRoomWhereStartTimeIsAfterEndTime() throws Exception {
        testRoom.isBusy(5, 15, 12, 10);
    }
    
    @Test
    public void testCheckAvailabilityForRoomWithDateInPast() throws Exception {
        // Get current date information
        java.util.Calendar currentDate = java.util.Calendar.getInstance();
        int currentMonth = currentDate.get(java.util.Calendar.MONTH) + 1;
        
        int pastMonth = (currentMonth > 1) ? currentMonth - 1 : 12;
        
        assertTrue("Past dates should throw an exception", testRoom.isBusy(pastMonth, 15, 10, 12));
    }
    
    @Test
    public void testAddMeetingWithOverlap() {
        Meeting overlapMeeting = new Meeting(5, 15, 11, 13, attendees, testRoom, "Overlap Meeting");
        try {
            testRoom.addMeeting(overlapMeeting);
            fail("Expected TimeConflictException was not thrown");
        } catch (TimeConflictException e) {
            assertTrue("Exception should mention room conflict", e.getMessage().contains("Conflict for room"));
        }
    }
    
    @Test
    public void testAddMeetingWithoutOverlap() throws TimeConflictException {
        Meeting nonOverlapMeeting = new Meeting(5, 15, 13, 15, attendees, testRoom, "Non-overlap Meeting");
        // Should not throw exception
        testRoom.addMeeting(nonOverlapMeeting);
        
        try {
            assertTrue("Room should be busy after adding meeting", testRoom.isBusy(5, 15, 13, 15));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    @Test(expected = TimeConflictException.class)
    public void testCheckAvailabilityForRoomWithInvalidTimeParameters() throws Exception {
        // Test with invalid time
        testRoom.isBusy(5, 15, 24, 26);
    }
}