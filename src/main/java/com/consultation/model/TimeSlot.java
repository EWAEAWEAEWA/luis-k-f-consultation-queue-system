package com.consultation.model;

import java.time.LocalTime;
import java.util.Objects; // Import for Objects.equals and Objects.hash

/**
 * Represents a specific time interval within a staff member's schedule.
 * A TimeSlot can be either available or booked with a single appointment.
 */
public class TimeSlot {

    /**
     * The start time of the slot.
     */
    private final LocalTime startTime;

    /**
     * The end time of the slot.
     */
    private final LocalTime endTime;

    /**
     * The Appointment currently booked in this slot, or null if available.
     */
    private Appointment appointment; // Only one appointment per slot

    /**
     * Flag indicating if the slot is generally marked as available for booking.
     * Even if true, the slot might be actually *booked* if `appointment` is not null.
     * Use `isAvailable()` for the effective availability check.
     */
    public boolean isMarkedAvailable; // Renamed for clarity

    /**
     * The Professor or Counselor this time slot belongs to.
     */
    private final User professorOrCounselor;

    /**
     * Constructs a new TimeSlot. Initially, it is marked as available and has no appointment.
     *
     * @param startTime          The start time for the slot. Must not be null.
     * @param endTime            The end time for the slot. Must not be null and must be after startTime.
     * @param professorOrCounselor The staff user this slot belongs to. Must not be null.
     * @throws NullPointerException if any argument is null.
     * @throws IllegalArgumentException if endTime is not after startTime.
     */
    public TimeSlot(LocalTime startTime, LocalTime endTime, User professorOrCounselor) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");
        Objects.requireNonNull(professorOrCounselor, "Professor/Counselor cannot be null");

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be strictly after start time.");
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.professorOrCounselor = professorOrCounselor;
        this.appointment = null; // Initially empty
        this.isMarkedAvailable = true; // Slots are generally available when created
    }

    /**
     * Checks if the time slot is effectively available for a new appointment.
     * A slot is available only if it's marked as available AND not currently booked.
     *
     * @return true if the slot is free and marked available, false otherwise.
     */
    public boolean isAvailable() {
<<<<<<< HEAD
        return isMarkedAvailable && appointment == null;
=======
        return appointment == null; // Only check if not booked
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
    }

    /**
     * Explicitly marks this slot as generally available or unavailable in the schedule.
     * Note: This does *not* automatically remove an existing appointment.
     * Use `removeAppointment()` to clear a booking.
     *
     * @param available true to mark as generally available, false otherwise.
     */
    public void setMarkedAvailable(boolean available) {
        this.isMarkedAvailable = available;
    }

    /**
     * Gets the start time of this slot.
     *
     * @return The start time.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of this slot.
     *
     * @return The end time.
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Checks if this time slot can accommodate an appointment of the specified duration.
     * The slot must be available, and its duration must be sufficient.
     *
     * @param durationMinutes The estimated duration of the appointment in minutes.
     * @return true if the slot is available and long enough, false otherwise.
     */
    public boolean canAccommodate(int durationMinutes) {
<<<<<<< HEAD
        if (durationMinutes <= 0) {
            return false;
        }
        long slotDurationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        long requiredSeconds = (long) durationMinutes * 60;
        return isAvailable() && slotDurationSeconds >= requiredSeconds;
=======
        // First check if duration is valid
        if (durationMinutes <= 0) {
            return false;
        }
        
        // Check if slot is available (not booked)
        if (appointment != null) {
            return false;
        }

        // Calculate and check duration
        long slotDurationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return slotDurationMinutes >= durationMinutes;
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
    }

    /**
     * Books this time slot with the given appointment.
     * The slot must be available and able to accommodate the appointment's duration.
     *
     * @param appointment The appointment to book into this slot. Must not be null.
     * @throws IllegalStateException if the slot is not available or cannot accommodate the appointment duration.
     * @throws NullPointerException if the appointment is null.
     */
    public void addAppointment(Appointment appointment) {
        Objects.requireNonNull(appointment, "Appointment cannot be null");
        if (!isAvailable()) { // Checks both marked available and appointment == null
            throw new IllegalStateException("Time slot at " + startTime + " is not available for booking.");
        }
        if (!canAccommodate(appointment.getEstimatedDuration())) {
            long slotDurationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
            throw new IllegalStateException("Time slot duration (" + slotDurationMinutes + " min) cannot accommodate the appointment duration (" + appointment.getEstimatedDuration() + " min).");
        }
        this.appointment = appointment;
<<<<<<< HEAD
        // Keep isMarkedAvailable as true, the booking is handled by appointment field.
        // If the intent was that booking makes the slot unavailable *in the schedule*,
        // you might uncomment the next line, but `isAvailable()` already covers this.
        // this.isMarkedAvailable = false; // --> Usually NOT needed here if using `isAvailable()` check
=======
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
    }

    /**
     * Gets the appointment currently booked in this slot.
     *
     * @return The Appointment object, or null if the slot is not booked.
     */
    public Appointment getAppointment() {
        return appointment;
    }

    /**
     * Removes the current appointment from this time slot, making it available again.
     * If the slot was previously marked as unavailable, it remains marked unavailable
     * but is now empty.
     */
    public void removeAppointment() {
        this.appointment = null;
<<<<<<< HEAD
        // Slot becomes effectively available again IF it was marked as available.
=======
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
    }

    /**
     * Checks if the slot is currently booked with an appointment.
     *
     * @return true if an appointment is present, false otherwise.
     */
    public boolean isBooked() {
        return appointment != null;
    }

    /**
     * Gets the Professor or Counselor this time slot belongs to.
     *
     * @return The User object representing the staff member.
     */
    public User getProfessorOrCounselor() {
        return professorOrCounselor;
    }

    /**
     * Compares this TimeSlot to another object for equality.
     * Two TimeSlots are considered equal if they belong to the same staff member
     * and have the same start and end times. The booking status (`appointment`)
     * and `isMarkedAvailable` flag are NOT considered for equality, as these represent
     * the state *within* the slot, not the slot's identity itself in the schedule.
     *
     * @param o The object to compare with.
     * @return true if the objects are equal TimeSlots, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Identity check
        if (o == null || getClass() != o.getClass()) return false; // Null and type check
        TimeSlot timeSlot = (TimeSlot) o;
        // Equality based on time range and the owning staff member
        return Objects.equals(startTime, timeSlot.startTime) &&
               Objects.equals(endTime, timeSlot.endTime) &&
               Objects.equals(professorOrCounselor, timeSlot.professorOrCounselor); // Relies on User.equals()
    }

    /**
     * Generates a hash code for this TimeSlot.
     * Based on the start time, end time, and the owning staff member to be consistent
     * with the `equals` method.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        // Hash code consistent with equals(): based on time range and owner
        return Objects.hash(startTime, endTime, professorOrCounselor); // Relies on User.hashCode()
    }

    /**
     * Returns a string representation of the TimeSlot, including its time range
     * and booking status. Useful for debugging.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        String status;
        if (appointment != null) {
            status = "Booked by " + appointment.getStudent().getUsername();
        } else if (isMarkedAvailable) {
            status = "Available";
        } else {
            status = "Marked Unavailable";
        }
        return "TimeSlot{" +
               "staff=" + professorOrCounselor.getUsername() +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", status=" + status +
               '}';
    }
}