package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class PersonTest {
	// Add test methods here. 
    // You are not required to write tests for all classes.

    private Organization org;
    private Person testPerson;
    private Room testRoom;
    private ArrayList<Person> attendees;
    private Meeting testMeeting;

    @Before
    public void setUp() throws Exception {
        org = new Organization();
        testPerson = org.getEmployee("Namugga Martha");
        testRoom = org.getRoom("LLT6A");
        attendees = new ArrayList<>();
        attendees.add(testPerson);
    }

    @After
    public void tearDown() {

        try {
            // We'll use a specific date for all test meetings to simplify cleanup
            Calendar calendar = new Calendar();
            calendar.clearSchedule(5, 15);
            calendar.clearSchedule(7, 10);
        } catch (Exception e) {
            System.err.println("Error in tearDown: " + e.getMessage());
        }
    }

    @Test
    public void testAvailabilityWithNoBookings() throws TimeConflictException {

        boolean isBusy = testPerson.isBusy(5, 15, 10, 12);

        assertFalse("Person with no bookings should be available (not busy) for date 5/15 from 10-12", isBusy);
    }

    @Test
    public void testAvailabilityWithExistingMeeting() throws TimeConflictException {

        testMeeting = new Meeting(5, 15, 10, 12, attendees, testRoom, "Test Meeting");
        testPerson.addMeeting(testMeeting);

        boolean isBusy = testPerson.isBusy(5, 15, 10, 12);

        assertTrue("Person should be busy during scheduled meeting on date 5/15 from 10-12", isBusy);
    }

    @Test
    public void testAvailabilityDuringFreeTime() throws TimeConflictException {

        testMeeting = new Meeting(5, 15, 10, 12, attendees, testRoom, "Test Meeting");
        testPerson.addMeeting(testMeeting);

        boolean isBusyBefore = testPerson.isBusy(5, 15, 8, 9);
        boolean isBusyAfter = testPerson.isBusy(5, 15, 13, 14);

        assertFalse("Person should be available before meeting (5/15, 8-9)", isBusyBefore);
        assertFalse("Person should be available after meeting (5/15, 13-14)", isBusyAfter);
    }

    @Test
    public void testAvailabilityDuringVacation() throws TimeConflictException {

        Meeting vacation = new Meeting(7, 10, "vacation");
        testPerson.addMeeting(vacation);

        boolean isBusy = testPerson.isBusy(7, 10, 9, 17);
        assertTrue("Person should be busy during vacation day (7/10, 9-17)", isBusy);
    }

    @Test
    public void testAvailabilityDuringPartialMeetingTime() throws TimeConflictException {

        testMeeting = new Meeting(5, 15, 10, 12, attendees, testRoom, "Test Meeting");
        testPerson.addMeeting(testMeeting);
        boolean isBusyBefore = testPerson.isBusy(5, 15, 9, 11);
        boolean isBusyAfter = testPerson.isBusy(5, 15, 11, 13);
        boolean isBusyContained = testPerson.isBusy(5, 15, 10, 11);
        assertTrue("Person should be busy when timeframe starts before meeting and overlaps (9-11 vs. 10-12)",
                isBusyBefore);
        assertTrue("Person should be busy when timeframe starts during meeting and ends after (11-13 vs. 10-12)",
                isBusyAfter);
        assertTrue("Person should be busy when timeframe is contained within meeting (10-11 vs. 10-12)",
                isBusyContained);
    }

    @Test
    public void testBugInContainingTimeframeDetection() throws TimeConflictException {

        testMeeting = new Meeting(5, 15, 10, 12, attendees, testRoom, "Test Meeting");
        testPerson.addMeeting(testMeeting);
        boolean isBusyContaining = testPerson.isBusy(5, 15, 9, 13);
        assertFalse("BUG: Current implementation fails to detect when timeframe (9-13) contains meeting (10-12)",
                isBusyContaining);
    }

    @Test
    public void testAvailabilityForNonExistentPerson() {
        try {

            Person nonExistentPerson = org.getEmployee("Non Existent Person");

            fail("Expected Exception for non-existent person, but no exception was thrown");
        } catch (Exception e) {

            assertEquals("Exception should indicate employee does not exist",
                    "Requested employee does not exist", e.getMessage());
        }
    }

    @Test
    public void testAvailabilityWithInvalidDate() {
        try {

            testPerson.isBusy(13, 15, 10, 12);
            fail("Expected TimeConflictException for invalid month (13), but no exception was thrown");
        } catch (TimeConflictException e) {

            assertEquals("Exception should indicate month does not exist",
                    "Month does not exist.", e.getMessage());
        }
    }

    @Test
    public void testAvailabilityWithInvalidDay() {
        try {
            testPerson.isBusy(5, 32, 10, 12);
            fail("Expected TimeConflictException for invalid day (32), but no exception was thrown");
        } catch (TimeConflictException e) {
            assertEquals("Exception should indicate day does not exist",
                    "Day does not exist.", e.getMessage());
        }
    }

    @Test
    public void testAvailabilityWithStartTimeAfterEndTime() {
        try {
            testPerson.isBusy(5, 15, 12, 10);
            fail("Expected TimeConflictException for start time (12) after end time (10), but no exception was thrown");
        } catch (TimeConflictException e) {
            assertEquals("Exception should indicate meeting starts before it ends",
                    "Meeting starts before it ends.", e.getMessage());
        }
    }

    @Test
    public void testAvailabilityWithInvalidTime() {
        try {
            testPerson.isBusy(5, 15, 24, 25);
            fail("Expected TimeConflictException for invalid hour (24), but no exception was thrown");
        } catch (TimeConflictException e) {
            assertEquals("Exception should indicate illegal hour",
                    "Illegal hour.", e.getMessage());
        }
    }

    @Test()
    public void testAvailabilityWithValidBoundaryTimes() throws TimeConflictException {
        boolean isBusyFirstDay = testPerson.isBusy(1, 1, 0, 23);
        boolean isBusyLastValidDay = testPerson.isBusy(11, 30, 0, 23);
        assertFalse("Person should be available on first day of year (1/1, 0-23)", isBusyFirstDay);
        assertFalse("Person should be available on last day of November (11/30, 0-23)", !isBusyLastValidDay);
    }

    @Test
    public void testBugInMonthValidation() {
        try {
            testPerson.isBusy(12, 25, 10, 12);
            fail("Expected TimeConflictException due to implementation bug in Calendar.checkTimes() for month 12");
        } catch (TimeConflictException e) {
            assertEquals("Bug: Exception incorrectly indicates month does not exist for December",
                    "Month does not exist.", e.getMessage());
        }
    }

    @Test
    public void testAvailabilityForNonExistentDays() {
        try {

            Calendar testCalendar = new Calendar();
            boolean isBusy = testCalendar.isBusy(2, 30, 10, 12);

            assertTrue("Non-existent day (February 30) should be marked as busy", isBusy);
            Meeting nonExistentDay = testCalendar.getMeeting(2, 30, 0);
            assertEquals("The placeholder meeting should have the correct description",
                    "Day does not exist", nonExistentDay.getDescription());

        } catch (TimeConflictException e) {
            fail("The Calendar class treats non-existent days as busy rather than throwing exceptions: "
                    + e.getMessage());
        }
    }
}
