package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class OrganizationTest {
     // Test for an empty room (no bookings)
     @Test
     public void testPrintAgendaForRoomWithNoBookings() {
         Room room = new Room("Room A");
         String agenda = room.printAgenda(4, 10); // Agenda for April 10th
         assertEquals("No bookings available.", agenda, "Agenda should be empty for rooms with no bookings.");
     }
 
     // Test for a room with a single booking
     @Test
     public void testPrintAgendaForRoomWithOneBooking() {
         Room room = new Room("Room A");
         room.bookMeeting("2025-04-10", "10:00", "12:00", "Meeting 1");
         String agenda = room.printAgenda(4, 10); // Agenda for April 10th
         assertTrue(agenda.contains("Meeting 1"), "Agenda should contain the meeting information.");
     }
 
     // Test for a room with multiple bookings on different days
     @Test
     public void testPrintAgendaForRoomWithMultipleBookings() {
         Room room = new Room("Room A");
         room.bookMeeting("2025-04-10", "10:00", "12:00", "Meeting 1");
         room.bookMeeting("2025-04-11", "14:00", "16:00", "Meeting 2");
         String agenda1 = room.printAgenda(4, 10); // Agenda for April 10th
         assertTrue(agenda1.contains("Meeting 1"), "Agenda should contain the first meeting.");
         String agenda2 = room.printAgenda(4, 11); // Agenda for April 11th
         assertTrue(agenda2.contains("Meeting 2"), "Agenda should contain the second meeting.");
     }
 
     // Test for invalid date range (start date is after end date)
     @Test
     public void testPrintAgendaWithInvalidDateRange() {
         Room room = new Room("Room A");
         assertThrows(IllegalArgumentException.class, () -> {
             room.printAgenda(5, 10); // Invalid date range (May 10th doesn't exist in the context)
         }, "Should throw an exception for an invalid date range.");
     }
 
     // Test for past dates - agenda should show no bookings for past dates
     @Test
     public void testPrintAgendaForPastDates() {
         Room room = new Room("Room A");
         room.bookMeeting("2025-04-10", "10:00", "12:00", "Meeting 1");
         String agenda = room.printAgenda(4, 9); // Agenda for April 9th (a past date)
         assertEquals("No bookings available.", agenda, "Agenda for past dates should show no bookings.");
     }
 
     // Edge Case: Print agenda for a non-existent room
     @Test
     public void testPrintAgendaForNonExistentRoom() {
         Room room = null; // Simulate non-existent room
         assertThrows(NullPointerException.class, () -> {
             room.printAgenda(4, 10); // Trying to print agenda for a non-existent room
         }, "Should throw a NullPointerException for a non-existent room.");
     }
 
     // Edge Case: Invalid meeting booking (start time after end time)
     @Test
     public void testBookMeetingWithInvalidTimes() {
         Room room = new Room("Room A");
         assertThrows(IllegalArgumentException.class, () -> {
             room.bookMeeting("2025-04-10", "14:00", "12:00", "Invalid Meeting");
         }, "Should throw an exception when the start time is after the end time.");
     }
 
     // Test for booking a meeting on a non-existent date (February 30th)
     @Test
     public void testPrintAgendaForInvalidDate() {
         Room room = new Room("Room A");
         assertThrows(IllegalArgumentException.class, () -> {
             room.printAgenda(2, 30); // February 30th is an invalid date
         }, "Should throw an exception for an invalid date (February 30th doesn't exist).");
     }
 
     // Test for booking conflict (double booking a room)
     @Test
     public void testDoubleBookingRoom() {
         Room room = new Room("Room A");
         room.bookMeeting("2025-04-10", "10:00", "12:00", "Meeting 1");
         assertThrows(TimeConflictException.class, () -> {
             room.bookMeeting("2025-04-10", "11:00", "13:00", "Meeting 2");
         }, "Should throw an exception for double booking the room.");
     }
 
     // Test for double booking a participant
     @Test
     public void testDoubleBookingParticipant() {
         Room room = new Room("Room A");
         room.bookMeeting("2025-04-10", "10:00", "12:00", "Meeting 1");
         room.bookMeeting("2025-04-10", "13:00", "14:00", "Meeting 2");
         assertThrows(TimeConflictException.class, () -> {
             room.bookMeeting("2025-04-10", "11:00", "13:00", "Meeting 3"); // Overlap with Meeting 1
         }, "Should throw an exception for double booking a participant.");
     }
 
     // Test booking a meeting that ends at midnight .
     @Test
     public void testBookMeetingEndingAtMidnight() {
         Room room = new Room("Room A");
         room.bookMeeting("2025-04-10", "10:00", "00:00", "Midnight Meeting");
         String agenda = room.printAgenda(4, 10); // Checking agenda for April 10th
         assertTrue(agenda.contains("Midnight Meeting"), "Agenda should contain the midnight-ending meeting.");
     }
 
     // Test for invalid duration (0 minutes)
     @Test
     public void testBookMeetingWithZeroDuration() {
         Room room = new Room("Room A");
         assertThrows(IllegalArgumentException.class, () -> {
             room.bookMeeting("2025-04-10", "10:00", "10:00", "Zero Duration Meeting");
         }, "Should throw an exception for a meeting with zero duration.");
     }
	
}
