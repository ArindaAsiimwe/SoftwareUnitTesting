package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MeetingTest {
	// Add test methods here. 

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    private Person person1;
    private Person person2;
    private Person person3;
    private Room room1;
    private Room room2;
    private ArrayList<Person> attendeeList;
    
    @Before
    public void setUp() {
        // Create test persons
        person1 = new Person("John Doe");
        person2 = new Person("Jane Smith");
        person3 = new Person("Bob Johnson");
        
        // Create test rooms
        room1 = new Room("LLT6A");
        room2 = new Room("LLT6B");
        
        // Initialize attendee list
        attendeeList = new ArrayList<>();
    }


    //NORMAL CASES
    @Test
    public void testBookValidMeeting() {
        // Test booking a valid meeting with available room and participants
        attendeeList.add(person1);
        attendeeList.add(person2);
        
        Meeting meeting = new Meeting(5, 15, 10, 12, attendeeList, room1, "Project Planning");
        
        assertEquals(5, meeting.getMonth());
        assertEquals(15, meeting.getDay());
        assertEquals(10, meeting.getStartTime());
        assertEquals(12, meeting.getEndTime());
        assertEquals(room1, meeting.getRoom());
        assertEquals("Project Planning", meeting.getDescription());
        assertEquals(2, meeting.getAttendees().size());
        assertTrue(meeting.getAttendees().contains(person1));
        assertTrue(meeting.getAttendees().contains(person2));
    }
    
    @Test
    public void testBookBackToBackMeetings() {
        // Test booking back-to-back meetings in the same room
        attendeeList.add(person1);
        Meeting meeting1 = new Meeting(6, 20, 9, 10, attendeeList, room1, "Campaign Kickoff");
        
        ArrayList<Person> attendeeList2 = new ArrayList<>();
        attendeeList2.add(person2);
        Meeting meeting2 = new Meeting(6, 20, 10, 11, attendeeList2, room1, "Creative Jam");
        
        // Verify meetings don't overlap - using the local helper method
        assertFalse(doMeetingsOverlap(meeting1, meeting2));
        
        // Verify meeting details
        assertEquals(room1, meeting1.getRoom());
        assertEquals(room1, meeting2.getRoom());
        assertEquals(6, meeting1.getMonth());
        assertEquals(6, meeting2.getMonth());
        assertEquals(20, meeting1.getDay());
        assertEquals(20, meeting2.getDay());
        assertEquals(9, meeting1.getStartTime());
        assertEquals(10, meeting1.getEndTime());
        assertEquals(10, meeting2.getStartTime());
        assertEquals(11, meeting2.getEndTime());
    }
    
    @Test
    public void testBookMeetingsInDifferentRooms() {
        // Test booking meetings for different rooms at the same time
        attendeeList.add(person1);
        Meeting meeting1 = new Meeting(7, 10, 14, 16, attendeeList, room1, "Sales Sprint");
        
        ArrayList<Person> attendeeList2 = new ArrayList<>();
        attendeeList2.add(person2);
        attendeeList2.add(person3);
        Meeting meeting2 = new Meeting(7, 10, 14, 16, attendeeList2, room2, "Dev Standup");
        
        // Verify meetings are in different rooms
        assertNotEquals(meeting1.getRoom(), meeting2.getRoom());
        
        // Verify meetings are at the same time
        assertEquals(meeting1.getMonth(), meeting2.getMonth());
        assertEquals(meeting1.getDay(), meeting2.getDay());
        assertEquals(meeting1.getStartTime(), meeting2.getStartTime());
        assertEquals(meeting1.getEndTime(), meeting2.getEndTime());
    }
    
    @Test
    public void testBookMeetingsWithMultipleParticipants() {
        // Test booking meetings with multiple participants
        attendeeList.add(person1);
        attendeeList.add(person2);
        attendeeList.add(person3);
        
        Meeting meeting = new Meeting(8, 5, 13, 15, attendeeList, room2, "Bug Bash");
        
        // Verify all attendees are included
        assertEquals(3, meeting.getAttendees().size());
        assertTrue(meeting.getAttendees().contains(person1));
        assertTrue(meeting.getAttendees().contains(person2));
        assertTrue(meeting.getAttendees().contains(person3));
        
        // Test removing an attendee
        meeting.removeAttendee(person2);
        assertEquals(2, meeting.getAttendees().size());
        assertFalse(meeting.getAttendees().contains(person2));
        
        // Test adding another attendee
        Person person4 = new Person("Alice Williams");
        meeting.addAttendee(person4);
        assertEquals(3, meeting.getAttendees().size());
        assertTrue(meeting.getAttendees().contains(person4));
    }
    
    // Helper method to check if two meetings overlap
    private boolean doMeetingsOverlap(Meeting m1, Meeting m2) {
        // Check if meetings are on the same day
        if (m1.getMonth() != m2.getMonth() || m1.getDay() != m2.getDay()) {
            return false;
        }
        
        // Check if time periods overlap
        return (m1.getStartTime() < m2.getEndTime() && m1.getEndTime() > m2.getStartTime());
    }

    
    //EDGE CASES
    @Test
    public void testBookMeetingAtMidnight() {
        // Test booking a meeting starting at midnight (00:00)
        Meeting meeting = new Meeting(5, 15, 0, 2, attendeeList, room1, "Early Morning Meeting");
        
        assertEquals(0, meeting.getStartTime());
        assertEquals(2, meeting.getEndTime());
        assertEquals("Early Morning Meeting", meeting.getDescription());
    }
    
    @Test
    public void testBookMeetingEndingAtMidnight() {
        // Test booking a meeting ending at midnight (00:00/24:00)
        Meeting meeting = new Meeting(5, 15, 22, 0, attendeeList, room1, "Late Night Meeting");
        
        assertEquals(22, meeting.getStartTime());
        assertEquals(0, meeting.getEndTime());
    }
    
    @Test
    public void testMeetingWithZeroDuration() {
        Calendar calendar = new Calendar();
    
        try {
            Meeting meeting = new Meeting(5, 15, 10, 10, attendeeList, room1, "Zero Duration Meeting");
            calendar.addMeeting(meeting);
            fail("Exception not thrown for zero duration");
        } catch (TimeConflictException e) {
            assertEquals("Meeting starts before it ends.", e.getMessage());
        }
    }
    
    
    @Test
    public void testMeetingWithNegativeDuration() {
        Calendar calendar = new Calendar();
        
        try {
            Meeting meeting = new Meeting(5, 15, 10, 8, attendeeList, room1, "Negative Duration Meeting");
            calendar.addMeeting(meeting);
            fail("Exception not thrown for negative duration");
        } catch (TimeConflictException e) {
            assertEquals("Meeting starts before it ends.", e.getMessage());
        }
    }

    @Test
    public void testMeetingWithNegativeTime() {
        Calendar calendar = new Calendar();
        
        try {
            Meeting meeting = new Meeting(5, 15, 10, -1, attendeeList, room1, "Invalid Time Meeting");
            calendar.addMeeting(meeting);
            fail("Exception not thrown for invalid time");
        } catch (TimeConflictException e) {
            assertEquals("Illegal hour.", e.getMessage());
        }
    }

    @Test
    public void testMeetingOnFeb29InLeapYear() {
        // Feb 29 in 2024 (a leap year)
        Meeting meeting = new Meeting(2, 29, 10, 12, attendeeList, room1, "Leap Day Meeting");
        
        assertEquals(2, meeting.getMonth());
        assertEquals(29, meeting.getDay());
    }
    
    @Test
    public void testMeetingOnFeb29InNonLeapYear() {
        Calendar calendar = new Calendar();
        
        try {
            Meeting meeting = new Meeting(2, 29, 10, 12, attendeeList, room1, "Invalid Date Meeting");
            calendar.addMeeting(meeting);
            fail("Exception not thrown for invalid date");
        } catch (TimeConflictException e) {
            assertEquals("Day does not exist.", e.getMessage());
        }
    }
    
    @Test
    public void testMeetingOnFeb30() {
        Calendar calendar = new Calendar();
    
        try {
            Meeting meeting = new Meeting(2, 30, 10, 12, attendeeList, room1, "Invalid Date Meeting");
            calendar.addMeeting(meeting);
            fail("Expection not thrown for invalid date");
        } catch (TimeConflictException e) {
            assertEquals("Day does not exist.", e.getMessage());
        }
    }    
    
    @Test
    public void testMeetingWithNoAttendees() {
        ArrayList<Person> emptyList = new ArrayList<>();
    
        try{
            new Meeting(8, 10, 9, 10, emptyList, room1, "Empty Meeting");
            fail("Expection not thrown for empty attendee list");
        } catch (IllegalArgumentException e) {
            assertEquals("At least one attendee required", e.getMessage());
        }
    }
    
    
    @Test
    public void testMeetingWithNullRoom() {
        try{
            new Meeting(8, 10, 9, 10, attendeeList, null, "Null Room Meeting");
            fail("Expection not thrown for null room");
        } catch (IllegalArgumentException e) {
            assertEquals("Room cannot be null", e.getMessage());
        }
    }
    
    @Test
    public void testMonthOutOfRange() {
        Calendar calendar = new Calendar();
        
        try {
            Meeting meeting = new Meeting(13, 10, 9, 10, attendeeList, room1, "Invalid Month Meeting");
            calendar.addMeeting(meeting);
            fail("Exception not thrown for invalid month");
        } catch (TimeConflictException e) {
            assertEquals("Month does not exist.", e.getMessage());
        }
    }

    
    @Test
    public void testTimeOutOfRange() {
        Calendar calendar = new Calendar();

        try {
            Meeting meeting = new Meeting(5, 15, 9, 25, attendeeList, room1, "Invalid Time Meeting");
            calendar.addMeeting(meeting);
            fail("Exception not thrown for invalid time");
        } catch (TimeConflictException e) {
            assertEquals("Illegal hour.", e.getMessage());
        }
    }

}

