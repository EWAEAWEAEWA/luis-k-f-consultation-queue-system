package com.consultation.controller;

import com.consultation.model.*; // Import all model classes including TimeSlot, User, etc.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter
import java.time.temporal.TemporalAdjusters;
import java.util.*; // Import common utilities like Map, List, Set, Collections, Comparator
import java.util.stream.Collectors; // Import Collectors

/**
 * Controller class for the Consultation Queue System.
 * Manages users, appointments, schedules, queues, and notifications.
 * Acts as the intermediary between the View (GUI) and the Model.
 */
public class ConsultationController {

    // Data Storage
    private final Map<String, User> users;
    private final Map<String, QueueManager> queues; // Maps Professor/Counselor username to their queue
    private final Map<String, Map<LocalDate, List<TimeSlot>>> professorSchedules; // username -> date -> slots
    private final Map<Integer, Appointment> appointments; // appointmentId -> Appointment
    private final Map<String, List<Notification>> userNotifications; // username -> List of Notifications

    // State Variables
    private int nextAppointmentId;

    // Formatter for user-friendly times in notifications
    private static final DateTimeFormatter NOTIFICATION_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
    private static final DateTimeFormatter SHORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Constructor initializes all data structures.
     */
    public ConsultationController() {
        users = new HashMap<>();
        queues = new HashMap<>();
        professorSchedules = new HashMap<>();
        appointments = new HashMap<>();
        userNotifications = new HashMap<>();
        nextAppointmentId = 1;
        // Time slots are typically initialized via DataInitializer after users are registered
    }

    // --- User Management ---

    /**
     * Registers a new user. Creates a queue and schedule map if the user is staff.
     *
     * @param username User's unique username.
     * @param password User's password.
     * @param role     User's role ("STUDENT", "PROFESSOR", "COUNSELOR").
     * @param name     User's full name.
     * @param email    User's email address.
     * @return The created User object, or null if the username already exists.
     */
    // --- START: Add this new private helper method inside ConsultationController ---
/**
 * Initializes default time slots (Mon-Fri, 9-12, 1-4) for the next 7 days for a specific staff user.
 * Skips dates if slots already exist.
 *
 * @param staffUser The PROFESSOR or COUNSELOR User object to initialize slots for.
 */
private void initializeDefaultSlotsForUser(User staffUser) {
    if (staffUser == null || (!staffUser.getRole().equals("PROFESSOR") && !staffUser.getRole().equals("COUNSELOR"))) {
        System.err.println("WARN: Attempted to initialize default slots for non-staff or null user: " + (staffUser != null ? staffUser.getUsername() : "null"));
        return;
    }
    String username = staffUser.getUsername();
    System.out.println("  Initializing default time slots for new staff: " + username);

    // Ensure the primary schedule map exists for this user
    Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.computeIfAbsent(username, k -> new HashMap<>());

    LocalDate today = LocalDate.now();

    for (int i = 0; i < 7; i++) { // Create for the next 7 days
        LocalDate date = today.plusDays(i);

        // Check if slots already exist for this specific day before attempting to add defaults
        schedule.computeIfAbsent(date, d -> {
            System.out.println("    -> Creating default slots for date: " + d);
            List<TimeSlot> slots = new ArrayList<>();
            // Define the standard default slots (e.g., 9-12, 1-4)
<<<<<<< HEAD
            slots.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), staffUser));
=======
            TimeSlot slot9am = new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), staffUser);
            System.out.println("      Created 9am slot: isMarkedAvailable=" + slot9am.isMarkedAvailable);
            slots.add(slot9am);
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
            slots.add(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0), staffUser));
            slots.add(new TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0), staffUser));
            slots.add(new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0), staffUser));
            slots.add(new TimeSlot(LocalTime.of(14, 0), LocalTime.of(15, 0), staffUser));
            slots.add(new TimeSlot(LocalTime.of(15, 0), LocalTime.of(16, 0), staffUser));
<<<<<<< HEAD
=======
            // Sort the slots by start time
            slots.sort(Comparator.comparing(TimeSlot::getStartTime));
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
            return slots; // Return the newly created list only if the key 'd' was absent
        });

        // This handles the case where computeIfAbsent was called but returned an existing list.
        // In such a case, the log message about creation wouldn't have printed,
        // so we log that we are skipping.
        if (!schedule.get(date).isEmpty() && !schedule.get(date).get(0).getStartTime().equals(LocalTime.of(9, 0))) { // Check if default was *not* just created
            System.out.println("    -> Slots already exist for date: " + date + " (Skipping default creation)");
        }
    }
    System.out.println("  Default slots initialization complete for: " + username);
}
// --- END: Add this new private helper method ---


// --- START: Modify the existing registerUser method ---
/**
 * Registers a new user. Creates a queue, schedule map, and default time slots if the user is staff.
 * Adds subject for Professors if provided.
 *
 * @param username User's unique username.
 * @param password User's password.
 * @param role     User's role ("STUDENT", "PROFESSOR", "COUNSELOR").
 * @param name     User's full name.
 * @param email    User's email address.
 * @param subject  Subject the professor teaches (can be null or empty, only relevant for PROFESSOR role).
 * @return The created User object, or null if the username already exists.
 */
// --- Modify the registerUser method in ConsultationController ---
/**
 * Registers a new user. Creates necessary structures for staff (queue, schedule, default slots)
 * and enrolls new students in all available subjects taught by professors.
 * Adds the specific subject for Professors if provided during registration.
 *
 * @param username User's unique username.
 * @param password User's password.
 * @param role     User's role ("STUDENT", "PROFESSOR", "COUNSELOR").
 * @param name     User's full name.
 * @param email    User's email address.
 * @param subject  Subject the professor teaches (only relevant for PROFESSOR role).
 * @return The created User object, or null if the username already exists.
 */
public User registerUser(String username, String password, String role, String name, String email, String subject) {
    if (username == null || username.trim().isEmpty() || password == null || password.isEmpty() || role == null) {
        System.err.println("Registration failed: Username, password, and role cannot be empty.");
        return null;
    }
    if (users.containsKey(username.trim())) {
        System.err.println("Registration failed: Username '" + username.trim() + "' already exists.");
        return null;
    }

    String trimmedUsername = username.trim();
    User user = new User(trimmedUsername, password, role, name, email);
    users.put(trimmedUsername, user);

    // --- Initialize Staff specifics ---
    if (role.equals("PROFESSOR") || role.equals("COUNSELOR")) {
        queues.put(trimmedUsername, new QueueManager(user));
        professorSchedules.put(trimmedUsername, new HashMap<>());
        System.out.println("Initialized queue and schedule map for staff: " + trimmedUsername);

        if (role.equals("PROFESSOR") && subject != null && !subject.trim().isEmpty()) {
            user.addSubject(subject.trim());
            System.out.println("  Added subject '" + subject.trim() + "' for Professor: " + trimmedUsername);
        } else if (role.equals("PROFESSOR")) {
            System.out.println("  Professor " + trimmedUsername + " registered without an initial subject.");
        }
        initializeDefaultSlotsForUser(user); // Initialize schedule slots

    }
    // --- START: Initialize Student specifics (Enroll in all Professor subjects) ---
    else if (role.equals("STUDENT")) {
        System.out.println("Enrolling new student '" + trimmedUsername + "' in available subjects...");
        // Use a Set to avoid adding duplicate subjects if multiple professors teach the same one
        Set<String> allProfessorSubjects = new HashSet<>();
        for (User potentialProfessor : users.values()) {
            if (potentialProfessor.getRole().equals("PROFESSOR")) {
                allProfessorSubjects.addAll(potentialProfessor.getSubjects());
            }
        }

        if (allProfessorSubjects.isEmpty()) {
             System.out.println("  No professor subjects found to enroll student in.");
        } else {
            for (String subj : allProfessorSubjects) {
                user.addSubject(subj); // Add each unique subject found
                System.out.println("  -> Enrolled student " + trimmedUsername + " in: " + subj);
            }
        }
    }
    // --- END: Initialize Student specifics ---

    System.out.println("User registered: " + trimmedUsername + " (" + role + ")");
    return user;
}
// --- END: Modify the existing registerUser method ---
    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful for user: " + username);
            return user;
        }
        System.out.println("Login failed for user: " + username);
        return null;
    }

    /**
     * Gets a list of all registered users.
     * Returns a new list to prevent external modification of the internal map's values.
     *
     * @return A List of all User objects.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // --- Schedule and Time Slot Management ---

    /**
     * Initializes default time slots for all registered professors/counselors
     * for the next 7 days. Skips dates/users if slots already exist.
     * Should typically be called once, e.g., by DataInitializer.
     */
    public void initializeAllTimeSlots() {
        System.out.println("\nInitializing default time slots (for next 7 days)...");
        LocalDate today = LocalDate.now();
        for (User user : users.values()) {
            if (user.getRole().equals("PROFESSOR") || user.getRole().equals("COUNSELOR")) {
                // Ensures the user has an entry in the schedule map
                Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.computeIfAbsent(user.getUsername(), k -> {
                    System.out.println("  (Creating schedule map entry for " + k + ")");
                    return new HashMap<>();
                });
                System.out.println("Initializing slots for: " + user.getUsername());
                for (int i = 0; i < 7; i++) {
                    LocalDate date = today.plusDays(i);
                    // Only add default slots if NO slots exist for that date yet
                    schedule.computeIfAbsent(date, d -> {
                        System.out.println("  -> Creating default slots for date: " + d);
                        List<TimeSlot> slots = new ArrayList<>();
                        // Example Default Slots (adjust times/duration as needed)
                        // Morning
                        slots.add(new TimeSlot(LocalTime.of(9, 0), LocalTime.of(10, 0), user));
                        slots.add(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(11, 0), user));
                        slots.add(new TimeSlot(LocalTime.of(11, 0), LocalTime.of(12, 0), user));
                        // Afternoon
                        slots.add(new TimeSlot(LocalTime.of(13, 0), LocalTime.of(14, 0), user));
                        slots.add(new TimeSlot(LocalTime.of(14, 0), LocalTime.of(15, 0), user));
                        slots.add(new TimeSlot(LocalTime.of(15, 0), LocalTime.of(16, 0), user));
                        return slots; // Return the newly created list
                    });
                }
            }
        }
        System.out.println("Default time slot initialization complete.");
    }

    /**
     * Gets *only available* time slots for a specific staff member on a given date, sorted.
     * Used primarily for student booking.
     *
     * @param username The username of the professor or counselor.
     * @param date     The date to check.
     * @return A List of available TimeSlot objects, sorted by start time, or an empty list.
     */
    public List<TimeSlot> getAvailableTimeSlots(String username, LocalDate date) {
        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(username);
        if (schedule != null) {
            List<TimeSlot> slotsOnDate = schedule.get(date);
            if (slotsOnDate != null) {
                LocalDateTime now = LocalDateTime.now();
                return slotsOnDate.stream()
                        .filter(TimeSlot::isAvailable) // Check if the slot is available (marked and not booked)
                        .filter(slot -> LocalDateTime.of(date, slot.getStartTime()).isAfter(now)) // Filter out past slots
                        .sorted(Comparator.comparing(TimeSlot::getStartTime)) // Ensure chronological order
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>(); // Return empty list if no schedule or slots found
    }


    // --- Appointment Management ---

    /**
     * Creates a new appointment by finding the next suitable available time slot.
     * Performs validations (including preventing duplicate active bookings) and sends
     * notifications upon successful creation.
     *
     * @param student            The student booking the appointment.
     * @param professorOrCounselor The staff member being booked.
     * @param subject            The subject/reason for the appointment.
     * @param duration           The estimated duration in minutes.
     * @return The created Appointment object, or null if creation failed.
     */
    public Appointment createAppointment(User student, User professorOrCounselor, String subject, int duration) {

        System.out.println("\nAttempting to create appointment:");
        System.out.println("  Student: " + student.getUsername() + ", Staff: " + professorOrCounselor.getUsername() + ", Subject: " + subject + ", Duration: " + duration);

        // 1. Basic Validations
        if (student == null || !student.getRole().equals("STUDENT")) {
            System.out.println("  FAIL: Invalid student.");
            return null;
        }
        if (professorOrCounselor == null || (!professorOrCounselor.getRole().equals("PROFESSOR") && !professorOrCounselor.getRole().equals("COUNSELOR"))) {
            System.out.println("  FAIL: Invalid staff member.");
            return null;
        }
        if (duration <= 0) {
            System.out.println("  FAIL: Invalid duration.");
            return null;
        }

        // 2. Subject Restriction Checks (Role-dependent)
        if (professorOrCounselor.getRole().equals("PROFESSOR")) {
            if (!professorOrCounselor.canTeach(subject)) {
                System.out.println("  FAIL: Professor '" + professorOrCounselor.getUsername() + "' cannot teach subject '" + subject + "'.");
                return null;
            }
            if (!student.isEnrolledIn(subject)) {
                System.out.println("  FAIL: Student '" + student.getUsername() + "' is not enrolled in subject '" + subject + "'.");
                return null;
            }
        } else if (professorOrCounselor.getRole().equals("COUNSELOR")) {
             // Allow any subject for counselor? Or restrict to "Academic Advising"?
             // Current logic: Only print info if not advising, but allow booking.
             if (!"Academic Advising".equalsIgnoreCase(subject)) {
                 System.out.println("  INFO: Booking non-advising subject '" + subject + "' with Counselor '" + professorOrCounselor.getUsername() + "'.");
             }
        }

        // 3. Check for Existing Pending/In-Progress Appointment with the SAME staff
        boolean alreadyBookedWithStaff = getUserAppointments(student).stream()
                .filter(app -> app.getProfessorOrCounselor().equals(professorOrCounselor)) // Check same staff
                .anyMatch(app -> "PENDING".equalsIgnoreCase(app.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(app.getStatus())); // Check active status

        if (alreadyBookedWithStaff) {
            System.out.println("  FAIL: Student '" + student.getUsername() + "' already has an active (PENDING or IN_PROGRESS) appointment with " + professorOrCounselor.getUsername() + ".");
            // Optional: Consider returning a specific error message or code if the GUI needs to display this exact reason.
            // For now, returning null will trigger the generic "Could not book" message in the GUI,
            // and the console log provides the specific reason.
            return null; // Prevent booking a duplicate active appointment with the same staff
        }

        // 4. Find Next Available Slot (Was step 3)
        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(professorOrCounselor.getUsername());
        if (schedule == null || schedule.isEmpty()) {
            System.out.println("  FAIL: Staff member has no schedule defined.");
            return null;
        }

        TimeSlot selectedSlot = null;
        LocalDateTime appointmentDateTime = null;
        LocalDateTime now = LocalDateTime.now();

        // Get sorted dates from today onwards
        List<LocalDate> sortedDates = schedule.keySet().stream()
                .filter(date -> !date.isBefore(LocalDate.now())) // Filter out past dates entirely
                .sorted()
                .collect(Collectors.toList());

        searchLoop:
        for (LocalDate date : sortedDates) {
<<<<<<< HEAD
            List<TimeSlot> slotsOnDate = schedule.get(date);
            if (slotsOnDate != null) {
                slotsOnDate.sort(Comparator.comparing(TimeSlot::getStartTime)); // Ensure slots are sorted by time
                for (TimeSlot slot : slotsOnDate) {
                    LocalDateTime slotStartDateTime = LocalDateTime.of(date, slot.getStartTime());
                    // Check if slot is available, can accommodate, and is in the future
                    if (slot.isAvailable() && slot.canAccommodate(duration) && slotStartDateTime.isAfter(now)) {
                        selectedSlot = slot;
                        appointmentDateTime = slotStartDateTime; // Use the actual start time of the slot
                        System.out.println("  SUCCESS: Found suitable slot: " + appointmentDateTime + " with " + professorOrCounselor.getUsername());
                        break searchLoop; // Found the earliest suitable slot
=======
            // Use getTimeSlotsForDate to get a properly sorted list of slots
            List<TimeSlot> slotsOnDate = getTimeSlotsForDate(professorOrCounselor.getUsername(), date);
            if (slotsOnDate != null && !slotsOnDate.isEmpty()) {
                System.out.println("  Checking slots for date: " + date);
                // The slots are already sorted by start time, so we'll find the earliest available one
                for (TimeSlot slot : slotsOnDate) {
                    LocalDateTime slotStartDateTime = LocalDateTime.of(date, slot.getStartTime());
                    System.out.println("    Checking slot at " + slot.getStartTime() + 
                        ": isAvailable=" + slot.isAvailable() + 
                        ", canAccommodate=" + slot.canAccommodate(duration) + 
                        ", isAfterNow=" + slotStartDateTime.isAfter(now));
                    
                    // Check if slot is available, can accommodate, and is in the future
                    if (slot.isAvailable() && 
                        slot.canAccommodate(duration) && 
                        slotStartDateTime.isAfter(now)) {
                        selectedSlot = slot;
                        appointmentDateTime = slotStartDateTime;
                        System.out.println("  SUCCESS: Found suitable slot: " + appointmentDateTime + " with " + professorOrCounselor.getUsername());
                        break searchLoop; // Break out of both loops immediately when we find a suitable slot
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
                    }
                }
            }
        }

        if (selectedSlot == null) {
            System.out.println("  FAIL: No suitable available time slots found for the requested duration.");
            return null;
        }

        // 5. Create and Finalize Appointment (Was step 4)
        Appointment appointment = new Appointment(nextAppointmentId++, student, professorOrCounselor, appointmentDateTime, subject, duration);
        try {
            selectedSlot.addAppointment(appointment); // Book the slot
            appointments.put(appointment.getId(), appointment); // Add to master appointment list

            QueueManager queue = queues.get(professorOrCounselor.getUsername());
            if (queue != null) {
                queue.addAppointment(appointment); // Add to the staff member's queue
                System.out.println("  Appointment added to queue for " + professorOrCounselor.getUsername());

                // Send Notifications
                String timeStr = appointmentDateTime.format(NOTIFICATION_TIME_FORMATTER);
                sendNotification(student, "Appointment booked with " + professorOrCounselor.getName() + " for " + subject + " on " + timeStr + ".");
                sendNotification(professorOrCounselor, "New appointment booked by " + student.getName() + " for " + subject + " on " + timeStr + ".");

                System.out.println("  Appointment created successfully (ID: " + appointment.getId() + ").");
                return appointment;
            } else {
                 // This is a critical internal error if a staff member doesn't have a queue
                 System.err.println("  CRITICAL ERROR: QueueManager missing for staff member " + professorOrCounselor.getUsername() + ". Aborting booking.");
                 selectedSlot.removeAppointment(); // Rollback slot booking
                 appointments.remove(appointment.getId()); // Rollback appointment map entry
                 nextAppointmentId--; // Rollback ID counter
                 return null;
             }
        } catch (IllegalStateException | NullPointerException e) {
             // Catch potential issues during slot booking or queue addition
             System.err.println("  FAIL: Error finalizing booking: " + e.getMessage());
             // Attempt to rollback state if partially successful
             if(appointments.containsKey(appointment.getId())) {
                 appointments.remove(appointment.getId());
                 nextAppointmentId--;
             }
             // We don't need to call selectedSlot.removeAppointment() if addAppointment failed.
             // If queue.addAppointment failed, the slot is booked but appt not in queue -> need rollback
             if(!selectedSlot.isAvailable() && selectedSlot.getAppointment() != null && selectedSlot.getAppointment().getId() == appointment.getId()){
                 selectedSlot.removeAppointment();
                 System.err.println("    Rolled back time slot booking.");
             }
             return null;
        }
    }

    /**
     * Cancels an existing appointment.
     * Removes it from the master list, frees the associated time slot,
     * removes it from the queue (if pending), and notifies relevant parties.
     *
     * @param appointment The Appointment object to cancel. Cannot be null.
     * @return true if cancellation was successful, false otherwise (e.g., appointment not found).
     */
    public boolean cancelAppointment(Appointment appointment) {
        if (appointment == null) {
             System.err.println("Cancel appointment failed: Appointment object is null.");
             return false;
         }
         int appointmentId = appointment.getId();
         System.out.println("Attempting to cancel appointment ID: " + appointmentId);

        // 1. Check if the appointment exists in the master list
        if (!appointments.containsKey(appointmentId)) {
             System.err.println("Cancel appointment failed: Appointment ID " + appointmentId + " not found.");
             return false;
         }

         // Appointment exists, proceed with cancellation steps
        Appointment removedAppointment = appointments.remove(appointmentId); // Remove from master list first

         if (removedAppointment != null) { // Should not be null if containsKey was true, but check anyway
             User student = removedAppointment.getStudent();
             User staff = removedAppointment.getProfessorOrCounselor();
             String subject = removedAppointment.getSubject();
             LocalDateTime appTime = removedAppointment.getAppointmentTime();

             // 2. Remove from Queue if it's still pending
             QueueManager queue = queues.get(staff.getUsername());
             if (queue != null) {
                 // QueueManager.removeAppointment handles checking both priority and regular queues
                 queue.removeAppointment(removedAppointment);
                 System.out.println("  Removed appointment ID " + appointmentId + " from queue for " + staff.getUsername());
             } else {
                  System.err.println("  WARN: QueueManager missing for staff " + staff.getUsername() + " during cancellation of appt ID " + appointmentId);
             }

             // 3. Free up the associated Time Slot
             boolean slotFreed = freeUpTimeSlot(removedAppointment);
             if (!slotFreed) {
                  System.err.println("  WARN: Associated time slot could not be freed for cancelled appointment ID " + appointmentId);
             } else {
                  System.out.println("  Freed time slot for cancelled appointment ID " + appointmentId);
             }

            // 4. Send Notifications (use removedAppointment data)
            String timeStr = appTime.format(NOTIFICATION_TIME_FORMATTER);
            sendNotification(student, "Your appointment with " + staff.getName() + " for '" + subject + "' on " + timeStr + " has been cancelled.");
            sendNotification(staff, "Your appointment with " + student.getName() + " for '" + subject + "' on " + timeStr + " has been cancelled.");

            System.out.println("Appointment ID " + appointmentId + " cancelled successfully.");
            return true; // Cancellation successful
        } else {
             System.err.println("Cancel appointment failed: Appointment ID " + appointmentId + " was unexpectedly null after removal attempt.");
             return false;
         }
    }


    /**
     * Retrieves the next available appointment from a staff member's queue (Priority first).
     * Updates the retrieved appointment's status to IN_PROGRESS.
     * Sends a notification to the student that their consultation is starting.
     * Handles the case where the queue is empty or the staff member is invalid.
     * **Important:** This method checks if the staff already has an appointment IN_PROGRESS.
     *
     * @param username The username of the professor or counselor.
     * @return The next Appointment object set to IN_PROGRESS, or null if the queue is empty, staff invalid, or already in a consultation.
     */
    public Appointment getNextAppointment(String username) {
        User staffUser = users.get(username);
        if (staffUser == null || (!staffUser.getRole().equals("PROFESSOR") && !staffUser.getRole().equals("COUNSELOR"))) {
             System.err.println("GetNextAppointment failed: Invalid staff username '" + username + "'.");
             return null;
         }
         QueueManager queue = queues.get(username);
         if (queue == null) {
              System.err.println("GetNextAppointment failed: No QueueManager found for staff '" + username + "'.");
              return null;
         }

         boolean alreadyInProgress = getUserAppointments(staffUser).stream()
                                       .anyMatch(app -> app.getStatus().equalsIgnoreCase("IN_PROGRESS"));
         if (alreadyInProgress) {
              System.out.println("GetNextAppointment info: Staff '" + username + "' is already in an IN_PROGRESS consultation.");
              return null;
         }

        Appointment nextAppointment = queue.getNextAppointment(); // poll() removes it from queue

        if (nextAppointment != null) {
             if (appointments.containsKey(nextAppointment.getId())) {
                nextAppointment.setStatus("IN_PROGRESS"); // Update status
                 System.out.println("Started appointment ID: " + nextAppointment.getId() + " for staff: " + username + ", student: " + nextAppointment.getStudent().getUsername());

                 sendNotification(nextAppointment.getStudent(), "Your consultation with " + nextAppointment.getProfessorOrCounselor().getName() + " regarding '" + nextAppointment.getSubject() + "' is starting now.");
                 return nextAppointment;
            } else {
                 System.err.println("CRITICAL ERROR: Appointment ID " + nextAppointment.getId() + " found in queue for '" + username + "' but not in the main appointments map!");
                 return null;
             }
        } else {
             System.out.println("Queue is empty for staff '" + username + "'. No next appointment.");
             return null;
         }
    }

    /**
     * Updates the status of an existing appointment.
     * Handles freeing the time slot if status becomes COMPLETED or CANCELLED.
     * Sends appropriate notifications for completion or cancellation transitions.
     *
     * @param appointment The Appointment object to update. Cannot be null.
     * @param newStatus   The new status string (e.g., "COMPLETED", "CANCELLED"). Cannot be null.
     * @return true if the update was successful, false otherwise (e.g., appointment not found).
     */
    public boolean updateAppointmentStatus(Appointment appointment, String newStatus) {
         if (appointment == null || newStatus == null) {
              System.err.println("Update appointment status failed: Null appointment or status provided.");
              return false;
          }
         int appointmentId = appointment.getId();
         String newStatusUpper = newStatus.trim().toUpperCase();
         if (newStatusUpper.isEmpty()) {
              System.err.println("Update appointment status failed: New status cannot be empty.");
              return false;
          }

         if (!appointments.containsKey(appointmentId)) {
             System.err.println("Update appointment status failed: Appointment ID " + appointmentId + " not found.");
             return false;
         }
         Appointment currentAppointment = appointments.get(appointmentId);
         String oldStatus = currentAppointment.getStatus();

        if (oldStatus.equals(newStatusUpper)) {
            System.out.println("Appointment ID " + appointmentId + " status is already " + newStatusUpper + ". No update needed.");
            return true;
        }
        currentAppointment.setStatus(newStatusUpper);
        System.out.println("Updated status for Appointment ID: " + appointmentId + " from '" + oldStatus + "' to '" + newStatusUpper + "'");

         User student = currentAppointment.getStudent();
         User staff = currentAppointment.getProfessorOrCounselor();
         String subject = currentAppointment.getSubject();
         LocalDateTime appTime = currentAppointment.getAppointmentTime();
         String timeStr = appTime.format(NOTIFICATION_TIME_FORMATTER);

         if ("COMPLETED".equals(newStatusUpper)) {
             boolean slotFreed = freeUpTimeSlot(currentAppointment);
             if (!slotFreed) System.err.println("  WARN: Could not free time slot for completed appointment ID: " + appointmentId);
              if (!"COMPLETED".equalsIgnoreCase(oldStatus)) {
                   sendNotification(student, "Your consultation with " + staff.getName() + " regarding '" + subject + "' on " + timeStr + " is complete.");
               }
         }
         else if ("CANCELLED".equals(newStatusUpper)) {
             boolean slotFreed = freeUpTimeSlot(currentAppointment);
              if (!slotFreed) System.err.println("  WARN: Could not free time slot for cancelled appointment ID: " + appointmentId);
              if (!"CANCELLED".equalsIgnoreCase(oldStatus)) {
                   sendNotification(student, "Your appointment with " + staff.getName() + " for '" + subject + "' on " + timeStr + " has been cancelled.");
                   sendNotification(staff, "Appointment with " + student.getName() + " for '" + subject + "' on " + timeStr + " has been cancelled.");
                    QueueManager queue = queues.get(staff.getUsername());
                    if(queue != null) queue.removeAppointment(currentAppointment);
               }
          }
        return true;
    }

/**
 * Sets or removes the priority status of an appointment.
 * If SETTING priority (and appointment is PENDING):
 * - Identifies the sequence of active (Pending) appointments starting
 *   from the earliest up to the target appointment ('shift group').
 * - Attempts to move the target appointment to the time slot of the *first* appointment in that sequence.
 * - Attempts to shift all other appointments in that sequence back one time slot relative to
 *   their original positions within the sequence (A moves to B's slot, B moves to C's slot, etc.).
 * - Updates the appointment's priority flag and moves it to the priority queue.
 * - Sends notifications about time changes and priority status.
 * If REMOVING priority (and appointment is PENDING):
 * - Simply updates the flag and moves it back to the regular queue (no time changes).
 * - Sends notification about priority status change.
 *
 * WARNING: This involves complex time slot reassignments. Rollback on failure is basic.
 *          This primarily works reliably on PENDING appointments. Changing priority
 *          of IN_PROGRESS appointments with this logic is generally not recommended.
 *
 * @param appointment The appointment instance whose priority status is to be changed. Cannot be null.
 * @param isPriority  The desired priority state: true to set as high priority, false.
 * @return true if the priority status was successfully updated (including potential time shifts), false otherwise.
 */
public boolean setPriority(Appointment appointment, boolean isPriority) {
    if (appointment == null) { System.err.println("Set priority failed: Null appointment."); return false; }
    if (!appointments.containsKey(appointment.getId())) { System.err.println("Set priority failed: Appointment ID " + appointment.getId() + " not found."); return false; }
    Appointment currentAppointment = appointments.get(appointment.getId()); // Use map instance

    String currentStatus = currentAppointment.getStatus();
    if (!"PENDING".equalsIgnoreCase(currentStatus)) {
         System.out.println("Set priority INFO: Can only perform time-swapping priority change on PENDING appointments. (Current status: " + currentStatus + ", ID " + currentAppointment.getId() + ")");
         return false;
    }

    User staff = currentAppointment.getProfessorOrCounselor();
    if (staff == null) { System.err.println("Set priority failed: Null staff ID: " + currentAppointment.getId()); return false; }
    QueueManager queueManager = queues.get(staff.getUsername());
    if (queueManager == null) { System.err.println("Set priority failed: No QueueManager for " + staff.getUsername()); return false; }

    boolean originalPriorityValue = currentAppointment.isPriority();

    if (originalPriorityValue == isPriority) {
        System.out.println("No priority change needed for appointment: " + currentAppointment.getId() + " (already " + isPriority + ")");
        return true; // Already in desired state
    }

    if (isPriority) { // Target: isPriority=true
        System.out.println("Attempting to SET priority (Swap & Shift) for PENDING appointment: " + currentAppointment.getId() + " scheduled at " + currentAppointment.getAppointmentTime());

        List<Appointment> pendingSortedAppointments = appointments.values().stream()
            .filter(app -> app.getProfessorOrCounselor().equals(staff) && "PENDING".equalsIgnoreCase(app.getStatus()))
            .sorted(Comparator.comparing(Appointment::getAppointmentTime)).collect(Collectors.toList());

        int targetIndex = -1;
        for (int i = 0; i < pendingSortedAppointments.size(); i++) {
            if (pendingSortedAppointments.get(i).getId() == currentAppointment.getId()) {
                targetIndex = i;
                break;
            }
        }
        if (targetIndex == -1) { System.err.println("Set priority failed: Target PENDING appointment " + currentAppointment.getId() + " unexpectedly not found in sorted list."); return false; }

        if (targetIndex == 0) {
            System.out.println("Appointment " + currentAppointment.getId() + " is already the earliest PENDING. Marking as priority (no time swap).");
            currentAppointment.setPriority(true);
            queueManager.setPriority(currentAppointment, true);
            sendNotification(currentAppointment.getStudent(), "Your appointment at " + currentAppointment.getAppointmentTime().format(NOTIFICATION_TIME_FORMATTER) + " is now high priority.");
            return true;
        }

        List<Appointment> shiftGroup = pendingSortedAppointments.subList(0, targetIndex + 1);
        System.out.println("Priority Shift group identified (" + shiftGroup.size() + " pending appointments up to index " + targetIndex + ")");

        Map<Integer, LocalDateTime> originalTimes = new HashMap<>();
        Map<Integer, TimeSlot> originalSlots = new HashMap<>();
        Map<Integer, Boolean> originalPriorities = new HashMap<>();
        System.out.println("Storing original state for shift group:");
        for (Appointment appInGroup : shiftGroup) {
            TimeSlot slot = findSlotForAppointment(appInGroup);
            if (slot == null) { System.err.println("Set priority PRE-CHECK FAILED: Cannot find original time slot for appointment " + appInGroup.getId() + ". Aborting."); return false; }
            originalTimes.put(appInGroup.getId(), appInGroup.getAppointmentTime());
            originalSlots.put(appInGroup.getId(), slot);
            originalPriorities.put(appInGroup.getId(), appInGroup.isPriority());
            System.out.println("  - Stored Original: App " + appInGroup.getId() + " at " + appInGroup.getAppointmentTime() + " in slot " + slot.getStartTime() + ", Priority=" + appInGroup.isPriority());
        }

        System.out.println("Freeing original slots for shift group...");
        for (Appointment appInGroup : shiftGroup) {
            TimeSlot slotToFree = originalSlots.get(appInGroup.getId());
            if (slotToFree != null && slotToFree.getAppointment() != null && slotToFree.getAppointment().getId() == appInGroup.getId()) {
                slotToFree.removeAppointment();
                System.out.println("  - Freed original slot (" + slotToFree.getStartTime() + ") for app " + appInGroup.getId());
            } else {
                System.err.println("WARN/ERROR: Could not properly free original slot for app " + appInGroup.getId() + ". State may be unexpected. Aborting swap.");
                System.err.println("  --- Attempting partial rollback of freed slots ---");
                for(Appointment appToRollback : shiftGroup){
                     if(appToRollback.getId() == appInGroup.getId()) break;
                     TimeSlot freedSlot = originalSlots.get(appToRollback.getId());
                     if(freedSlot != null && freedSlot.isAvailable()){
                         try{ freedSlot.addAppointment(appToRollback); System.err.println("    - Rolled back slot for app " + appToRollback.getId()); }
                         catch(Exception rbEx){ System.err.println("    - Rollback error for app "+appToRollback.getId()+": "+rbEx.getMessage()); }
                     }
                 }
                return false;
            }
        }
        System.out.println("Original slots freed. Proceeding with time/slot reassignment...");

        try {
            System.out.println("  Shifting intermediate appointments...");
            for (int i = 0; i < targetIndex; i++) {
                Appointment appToMove = shiftGroup.get(i);
                Appointment appProvidingSlot = shiftGroup.get(i + 1);
                LocalDateTime newTime = originalTimes.get(appProvidingSlot.getId());
                TimeSlot targetSlot = originalSlots.get(appProvidingSlot.getId());

                if (targetSlot == null) throw new IllegalStateException("Critical Error during swap: Original slot mapping missing for app " + appProvidingSlot.getId());
                System.out.println("    - Moving App " + appToMove.getId() + " to time " + newTime + " (original slot of App " + appProvidingSlot.getId() + ")");

                if (!targetSlot.isAvailable() || !targetSlot.canAccommodate(appToMove.getEstimatedDuration())) {
                     throw new IllegalStateException("SWAP PRE-BOOK CHECK FAILED: Target slot "+ targetSlot.getStartTime() + " for app " + appToMove.getId() + " became unavailable or too small during swap!");
                }
                appToMove.setAppointmentTime(newTime);
                targetSlot.addAppointment(appToMove);
                sendNotification(appToMove.getStudent(), "Your appointment time with " + staff.getName() + " was adjusted to " + newTime.format(NOTIFICATION_TIME_FORMATTER) + " due to a queue priority change.");
            }

            System.out.println("  Moving prioritized appointment...");
            Appointment prioritizedApp = shiftGroup.get(targetIndex);
            Appointment firstAppInGroup = shiftGroup.get(0);
            LocalDateTime firstOriginalTime = originalTimes.get(firstAppInGroup.getId());
            TimeSlot firstOriginalSlot = originalSlots.get(firstAppInGroup.getId());

            if (firstOriginalSlot == null) throw new IllegalStateException("Critical Error during swap: Original first slot mapping missing for app " + firstAppInGroup.getId());
            System.out.println("    - Moving Prioritized App " + prioritizedApp.getId() + " to time " + firstOriginalTime + " (original slot of App " + firstAppInGroup.getId() + ")");

            if (!firstOriginalSlot.isAvailable() || !firstOriginalSlot.canAccommodate(prioritizedApp.getEstimatedDuration())) {
                throw new IllegalStateException("SWAP PRE-BOOK CHECK FAILED: Original first slot "+ firstOriginalSlot.getStartTime() + " for prioritized app " + prioritizedApp.getId() + " became unavailable or too small during swap!");
            }
            prioritizedApp.setAppointmentTime(firstOriginalTime);
            firstOriginalSlot.addAppointment(prioritizedApp);

            System.out.println("  Updating priority flag and queue manager...");
            prioritizedApp.setPriority(true);
            queueManager.setPriority(prioritizedApp, true);

            System.out.println("Successfully set priority for appointment " + prioritizedApp.getId() + " and shifted others.");
            sendNotification(prioritizedApp.getStudent(), "Your appointment with " + staff.getName() + " at " + prioritizedApp.getAppointmentTime().format(NOTIFICATION_TIME_FORMATTER) + " is now high priority.");

            return true; // Success!

        } catch (Exception e) {
            System.err.println("<<<<<<<<<<<<<<< ERROR during priority reassignment phase: " + e.getMessage() + " >>>>>>>>>>>>>>>");
            System.err.println("<<<<<<<<<<<<<<< ATTEMPTING ROLLBACK from stored original state... >>>>>>>>>>>>>>>");
            e.printStackTrace();

            for (Map.Entry<Integer, TimeSlot> entry : originalSlots.entrySet()) {
                 int appIdToRestore = entry.getKey();
                 TimeSlot originalSlot = entry.getValue();
                 LocalDateTime originalTime = originalTimes.get(appIdToRestore);
                 boolean originalPrio = originalPriorities.getOrDefault(appIdToRestore, false);
                 Appointment appToRestore = appointments.get(appIdToRestore);

                 if (appToRestore != null && originalSlot != null && originalTime != null) {
                     System.err.println("  Rollback for App ID: " + appIdToRestore);
                      TimeSlot incorrectSlot = findSlotForAppointment(appToRestore);
                      if (incorrectSlot != null && !incorrectSlot.equals(originalSlot)) {
                         System.err.println("    - Found in incorrect slot (" + incorrectSlot.getStartTime() + "), freeing it.");
                         incorrectSlot.removeAppointment();
                      } else if (incorrectSlot == null && !originalSlot.isAvailable() && originalSlot.getAppointment()!= null && originalSlot.getAppointment().getId() != appIdToRestore ){
                          System.err.println("    - WARNING: Original slot occupied by unexpected App ID "+ originalSlot.getAppointment().getId()+". Cannot forcibly restore.");
                          continue;
                      } else if (incorrectSlot == null){
                           System.err.println("    - App wasn't found in any slot.");
                       }

                      appToRestore.setAppointmentTime(originalTime);
                      appToRestore.setPriority(originalPrio);
                       System.err.println("    - Reset time to " + originalTime + ", priority to " + originalPrio);

                      try {
                          if (originalSlot.isAvailable()) {
                              originalSlot.addAppointment(appToRestore);
                              System.err.println("    - SUCCESS: Restored App " + appIdToRestore + " to original slot " + originalSlot.getStartTime());
                          } else if (originalSlot.getAppointment() != null && originalSlot.getAppointment().getId() == appIdToRestore) {
                              System.err.println("    - INFO: App " + appIdToRestore + " already correctly in original slot " + originalSlot.getStartTime() + ".");
                          } else {
                              System.err.println("    - *** ROLLBACK FAILED: Original slot " + originalSlot.getStartTime() + " is unexpectedly occupied! By AppID: " + (originalSlot.getAppointment()!=null ? originalSlot.getAppointment().getId() : "null"));
                           }
                      } catch (Exception rollbackEx) {
                           System.err.println("    - *** ROLLBACK EXCEPTION trying to book original slot " + originalSlot.getStartTime() + ": " + rollbackEx.getMessage());
                       }

                       queueManager.setPriority(appToRestore, appToRestore.isPriority());

                  } else {
                      System.err.println("  Skipping rollback for App ID: " + appIdToRestore + " (incomplete original data)");
                   }
              }
              System.err.println("<<<<<<<<<<<<<<< ROLLBACK ATTEMPT COMPLETE. Final state may be inconsistent. >>>>>>>>>>>>>>>");
              return false;
         } // End try-catch for reassignment


     } else { // Target: isPriority=false
        System.out.println("Attempting to REMOVE priority for PENDING appointment: " + currentAppointment.getId());
        currentAppointment.setPriority(false); // Update flag
        boolean moved = queueManager.setPriority(currentAppointment, false); // Update queue manager

        if (moved) {
             System.out.println("Removed priority status for appointment " + currentAppointment.getId());
             sendNotification(currentAppointment.getStudent(), "The high priority status for your appointment with " + staff.getName() + " on " + currentAppointment.getAppointmentTime().format(NOTIFICATION_TIME_FORMATTER) + " has been removed.");
             return true; // Success
        } else {
             System.err.println("Remove priority failed: Could not update queue manager for ID: " + currentAppointment.getId());
             currentAppointment.setPriority(true); // Revert flag change on failure
             return false; // Indicate failure
        }
     }
 }

    // --- Schedule Management (Enhanced) ---

    /**
     * Gets ALL time slots (available and booked) for a specific staff member on a given date,
     * sorted chronologically. Returns an empty list if staff/date not found or not applicable.
     *
     * @param username The username of the professor or counselor.
     * @param date     The date to check.
     * @return A List of all TimeSlot objects for that date, sorted by start time, or an empty list.
     */
    public List<TimeSlot> getTimeSlotsForDate(String username, LocalDate date) {
        User staffUser = users.get(username);
        if (staffUser == null || (!staffUser.getRole().equals("PROFESSOR") && !staffUser.getRole().equals("COUNSELOR"))) {
            return new ArrayList<>();
        }
        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(username);
        if (schedule != null) {
            List<TimeSlot> slotsOnDate = schedule.get(date);
            if (slotsOnDate != null) {
<<<<<<< HEAD
                return slotsOnDate.stream()
                        .sorted(Comparator.comparing(TimeSlot::getStartTime))
                        .collect(Collectors.toList());
=======
                // Create a new sorted list to avoid modifying the original
                List<TimeSlot> sortedSlots = new ArrayList<>(slotsOnDate);
                sortedSlots.sort(Comparator.comparing(TimeSlot::getStartTime));
                return sortedSlots;
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
            }
        }
        return new ArrayList<>();
    }

    /**
     * Adds a new available time slot for a staff member on a specific date.
     * Performs validation checks for overlaps, valid times, and user validity.
     *
     * @param staffUser The User object for the professor or counselor. Must be a valid staff user present in the system.
     * @param date      The date for the new slot. Must not be null.
     * @param startTime The start time of the new slot. Must not be null.
     * @param endTime   The end time of the new slot. Must not be null and must be after startTime.
     * @return true if the slot was added successfully, false otherwise.
     */
    public boolean addTimeSlot(User staffUser, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (staffUser == null || date == null || startTime == null || endTime == null) {
            System.err.println("Add Time Slot failed: Null input parameter(s).");
            return false;
        }
        if (!users.containsKey(staffUser.getUsername()) ||
            (!staffUser.getRole().equals("PROFESSOR") && !staffUser.getRole().equals("COUNSELOR"))) {
             System.err.println("Add Time Slot failed: Provided user '" + staffUser.getUsername() + "' is not a valid staff member in the system.");
            return false;
        }
        if (!endTime.isAfter(startTime)) {
            System.err.println("Add Time Slot failed for " + staffUser.getUsername() + " on " + date + ": End time [" + endTime + "] must be after start time [" + startTime + "].");
            return false;
        }
        LocalDateTime newSlotStartDateTime = LocalDateTime.of(date, startTime);
        if (newSlotStartDateTime.isBefore(LocalDateTime.now().plusMinutes(1))) {
            System.err.println("Add Time Slot failed for " + staffUser.getUsername() + " on " + date + ": Cannot add slots starting in the past or within the next minute [" + newSlotStartDateTime.toLocalTime() + "].");
            return false;
        }

        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.computeIfAbsent(staffUser.getUsername(), k -> new HashMap<>());
        List<TimeSlot> existingSlotsOnDate = schedule.computeIfAbsent(date, d -> new ArrayList<>());

        for (TimeSlot existing : existingSlotsOnDate) {
            if (startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime())) {
                System.err.println("Add Time Slot failed for " + staffUser.getUsername() + " on " + date + ": New slot [" + startTime + "-" + endTime + "] overlaps with existing slot [" + existing.getStartTime() + "-" + existing.getEndTime() + "].");
                return false;
            }
        }

        TimeSlot newSlot = new TimeSlot(startTime, endTime, staffUser);
        existingSlotsOnDate.add(newSlot);
        existingSlotsOnDate.sort(Comparator.comparing(TimeSlot::getStartTime));

        System.out.println("Controller: Added Time Slot for " + staffUser.getUsername() + " on " + date + " from " + startTime + " to " + endTime);

        String formattedMessage = String.format("Availability added: %s from %s to %s.",
                date.toString(),
                startTime.format(SHORT_TIME_FORMATTER),
                endTime.format(SHORT_TIME_FORMATTER)
        );
        sendNotification(staffUser, formattedMessage);
        return true;
    }

    /**
     * Removes an *available* time slot for a staff member.
     * Validates inputs and fails if the slot does not exist, is booked, or user is invalid.
     *
     * @param staffUser        The User object for the professor or counselor. Must be a valid staff user.
     * @param date             The date of the slot to remove. Must not be null.
     * @param timeSlotToRemove The specific TimeSlot object to remove (must match based on TimeSlot.equals method). Must not be null.
     * @return true if the slot was successfully found, was available, and was removed, false otherwise.
     */
    public boolean removeTimeSlot(User staffUser, LocalDate date, TimeSlot timeSlotToRemove) {
        if (staffUser == null || date == null || timeSlotToRemove == null) {
            System.err.println("Remove Time Slot failed: Null input parameter(s).");
            return false;
        }
        if (!users.containsKey(staffUser.getUsername()) ||
            (!staffUser.getRole().equals("PROFESSOR") && !staffUser.getRole().equals("COUNSELOR"))) {
            System.err.println("Remove Time Slot failed: Provided user '" + staffUser.getUsername() + "' is not a valid staff member in the system.");
            return false;
        }

        Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(staffUser.getUsername());
        if (schedule == null) {
            System.err.println("Remove Time Slot failed: No schedule map found for user " + staffUser.getUsername() + ".");
            return false;
        }
        List<TimeSlot> slotsOnDate = schedule.get(date);
        if (slotsOnDate == null || slotsOnDate.isEmpty()) {
            System.err.println("Remove Time Slot failed: No slots found for date " + date + " for user " + staffUser.getUsername() + ".");
            return false;
        }

        Optional<TimeSlot> foundSlotOptional = slotsOnDate.stream()
                                                      .filter(slot -> slot.equals(timeSlotToRemove))
                                                      .findFirst();

        if (!foundSlotOptional.isPresent()) {
            System.err.println("Remove Time Slot failed: Slot matching " + timeSlotToRemove.getStartTime() + "-" + timeSlotToRemove.getEndTime() + " not found in the schedule for " + date + " for user " + staffUser.getUsername() + ".");
            return false;
        }

        TimeSlot slotInstanceToRemove = foundSlotOptional.get();

        if (!slotInstanceToRemove.isAvailable()) {
            String bookerInfo = "a booking";
             if(slotInstanceToRemove.getAppointment() != null && slotInstanceToRemove.getAppointment().getStudent() != null){
                 bookerInfo = "an appointment for " + slotInstanceToRemove.getAppointment().getStudent().getName();
             }
            System.err.println("Remove Time Slot failed: Slot " + slotInstanceToRemove.getStartTime() + "-" + slotInstanceToRemove.getEndTime() + " on " + date + " for " + staffUser.getUsername() + " cannot be removed because it has " + bookerInfo + ".");
            return false;
        }

        boolean removed = slotsOnDate.remove(slotInstanceToRemove);

        if (removed) {
            System.out.println("Controller: Removed available Time Slot for " + staffUser.getUsername() + " on " + date + " from " + slotInstanceToRemove.getStartTime() + " to " + slotInstanceToRemove.getEndTime());

            String formattedMessage = String.format("Availability removed: %s from %s to %s.",
                    date.toString(),
                    slotInstanceToRemove.getStartTime().format(SHORT_TIME_FORMATTER),
                    slotInstanceToRemove.getEndTime().format(SHORT_TIME_FORMATTER)
            );
            sendNotification(staffUser, formattedMessage);

            if (slotsOnDate.isEmpty()) {
                schedule.remove(date);
                System.out.println("  -> Date entry " + date + " removed from schedule map as it's now empty.");
            }
            return true;
        } else {
             System.err.println("Remove Time Slot failed: Internal error - slot found but not removed for " + staffUser.getUsername() + " on " + date + " " + slotInstanceToRemove.getStartTime() + ".");
            return false;
        }
    }

    // --- Helper methods ---
    /** Helper method to find the TimeSlot object currently holding a specific appointment. */
    private TimeSlot findSlotForAppointment(Appointment appointment) {
         if (appointment == null) return null;
         User staff = appointment.getProfessorOrCounselor();
         LocalDateTime appTime = appointment.getAppointmentTime();
         if (staff == null || appTime == null) return null;

         Map<LocalDate, List<TimeSlot>> schedule = professorSchedules.get(staff.getUsername());
         if (schedule != null) {
             List<TimeSlot> slotsOnDate = schedule.get(appTime.toLocalDate());
             if (slotsOnDate != null) {
                 for (TimeSlot slot : slotsOnDate) {
                     if (slot.getStartTime().equals(appTime.toLocalTime()) &&
                         slot.getAppointment() != null &&
                         slot.getAppointment().getId() == appointment.getId()) {
                          return slot;
                      }
                  }
              }
          }
         System.err.println("Warn: findSlotForAppointment couldn't find matching slot for Appointment ID " + appointment.getId() + " scheduled at " + appTime);
         return null;
     }

    /** Helper method to find and free the TimeSlot associated with a given appointment. */
    private boolean freeUpTimeSlot(Appointment appointment) {
        if (appointment == null) {
            System.err.println("freeUpTimeSlot: Cannot free slot for null appointment.");
            return false;
        }
        TimeSlot slot = findSlotForAppointment(appointment);
        if (slot != null) {
             if (!slot.isAvailable() && slot.getAppointment() != null && slot.getAppointment().getId() == appointment.getId()){
                 slot.removeAppointment();
                 System.out.println("  Freed time slot (" + slot.getStartTime() + " on " + appointment.getAppointmentTime().toLocalDate() + ") for appointment ID " + appointment.getId());
                 return true;
             } else {
                  System.err.println("  WARN: freeUpTimeSlot found a slot object, but it wasn't booked by appointment ID " + appointment.getId() + " as expected. Slot status: " + (slot.isAvailable()? "Available":"Booked by " + (slot.getAppointment() != null ? slot.getAppointment().getId() : "null")) );
                  return false;
             }
        } else {
            System.err.println("  WARN: freeUpTimeSlot couldn't find the associated time slot for appointment ID " + appointment.getId() + " to free it.");
            return false;
        }
    }

    // --- Queue and Information Retrieval ---

    /** Gets all appointments associated with a user (as student or staff), sorted by time. */
    public List<Appointment> getUserAppointments(User user) {
        if (user == null) return new ArrayList<>();
        String username = user.getUsername();
        return appointments.values().stream()
                .filter(app -> (app.getStudent() != null && app.getStudent().getUsername().equals(username)) ||
                                (app.getProfessorOrCounselor() != null && app.getProfessorOrCounselor().getUsername().equals(username)))
                .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                .collect(Collectors.toList());
    }

    /** Gets the current total queue size (pending priority + regular) for a staff member. */
    public int getQueueSize(String username) {
        QueueManager q = queues.get(username);
        return (q != null) ? q.getQueueSize() : 0; // Return 0 if no queue manager
    }

    /** Gets the QueueManager instance for a staff member. */
    public QueueManager getQueueManager(String username) {
        return queues.get(username); // Returns null if user isn't staff or not found
    }

    /** Gets the estimated total wait time in minutes for a staff member's queue. */
    public int getEstimatedWaitTime(String username) {
        QueueManager q = queues.get(username);
        return (q != null) ? q.getEstimatedWaitTime() : 0; // Return 0 if no queue manager
    }

    // --- Notification Management ---

    /** Sends a notification to a specific user. */
    private void sendNotification(User user, String message) {
        if (user == null || message == null || message.trim().isEmpty()) return;
        Notification notification = new Notification(LocalDateTime.now(), message);
        userNotifications.computeIfAbsent(user.getUsername(), k -> new ArrayList<>()).add(notification);
        System.out.println(">>> Controller: Sent notification to " + user.getUsername() + ": \"" + message + "\"");
    }

    /** Retrieves a list of UNREAD notifications for a user, sorted newest first. */
    public List<Notification> getUserNotifications(String username) {
        return userNotifications.getOrDefault(username, new ArrayList<>()) // Get list or empty if none
               .stream()
               .filter(n -> !n.isRead()) // Keep only unread notifications
               .sorted(Comparator.comparing(Notification::getTimestamp).reversed()) // Sort by timestamp descending
               .collect(Collectors.toList());
    }

    /** Marks a single notification as read by its ID. */
    public boolean markSingleNotificationAsRead(String username, String notificationId) {
        if (username == null || notificationId == null) {
            System.err.println("Mark single notification read failed: Null username or notification ID.");
            return false;
        }
        List<Notification> userNots = userNotifications.get(username);
        if (userNots != null) {
            for (Notification n : userNots) {
                if (notificationId.equals(n.getId())) { // Find by unique ID
                    if (!n.isRead()) {
                        n.setRead(true); // Mark as read
                        System.out.println("Marked notification ID " + notificationId + " as read for user " + username);
                        return true; // Success
                    } else {
                        System.out.println("Notification ID " + notificationId + " was already read for user " + username);
                        return true; // Already read, still considered success
                    }
                }
            }
            System.err.println("Mark single notification read failed: Notification ID '" + notificationId + "' not found for user " + username);
            return false;
        } else {
             System.err.println("Mark single notification read failed: No notifications found for user " + username);
             return false;
         }
    }

     /** Marks all notifications for a user as read. */
     public void markAllNotificationsAsRead(String username) {
        if (username == null) return;
        List<Notification> userNots = userNotifications.get(username);
        if (userNots != null && !userNots.isEmpty()) {
            int count = 0;
            for (Notification n : userNots) {
                if (!n.isRead()) { // Only mark if currently unread
                    n.setRead(true);
                    count++;
                }
            }
            if (count > 0) {
                 System.out.println("Marked " + count + " notifications as read for user " + username);
             } else {
                 System.out.println("No unread notifications found to mark as read for user " + username);
             }
        } else {
             System.out.println("No notifications found for user " + username);
         }
     }
// Add these methods inside the ConsultationController class

/**
 * Calculates basic analytics for a given staff member based on their completed appointments.
 *
 * @param staffUser The User object for the professor or counselor.
 * @return A Map containing analytics data (keys defined below) or an empty map if no data.
 *         Keys: "totalCompletedWeek", "totalCompletedMonth", "averageDurationMinutes",
 *               "mostFrequentSubject", "peakDayOfWeek"
 */
public Map<String, Object> getStaffAnalytics(User staffUser) {
    Map<String, Object> analytics = new HashMap<>();
    if (staffUser == null || (!staffUser.getRole().equals("PROFESSOR") && !staffUser.getRole().equals("COUNSELOR"))) {
        return analytics; // Return empty map if not valid staff
    }
    String username = staffUser.getUsername();
    LocalDateTime now = LocalDateTime.now();
    LocalDate startOfWeek = now.toLocalDate().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    LocalDate startOfMonth = now.toLocalDate().withDayOfMonth(1);

    List<Appointment> completedAppointments = appointments.values().stream()
            .filter(app -> app.getProfessorOrCounselor().getUsername().equals(username))
            .filter(app -> "COMPLETED".equalsIgnoreCase(app.getStatus()))
            .collect(Collectors.toList());

    if (completedAppointments.isEmpty()) {
        analytics.put("totalCompletedWeek", 0L);
        analytics.put("totalCompletedMonth", 0L);
        analytics.put("averageDurationMinutes", 0.0);
        analytics.put("mostFrequentSubject", "N/A");
        analytics.put("peakDayOfWeek", "N/A");
        return analytics; // Return default values if no completed appointments
    }

    long totalCompletedWeek = completedAppointments.stream()
            .filter(app -> !app.getAppointmentTime().toLocalDate().isBefore(startOfWeek))
            .count();
    long totalCompletedMonth = completedAppointments.stream()
            .filter(app -> !app.getAppointmentTime().toLocalDate().isBefore(startOfMonth))
            .count();

    double averageDurationMinutes = completedAppointments.stream()
            .mapToInt(Appointment::getEstimatedDuration)
            .average()
            .orElse(0.0);

    String mostFrequentSubject = "N/A";
    if (staffUser.getRole().equals("PROFESSOR")) {
        mostFrequentSubject = completedAppointments.stream()
                .filter(app -> app.getSubject() != null && !app.getSubject().trim().isEmpty())
                 .collect(Collectors.groupingBy(Appointment::getSubject, Collectors.counting())) // Group by subject, count occurrences
                .entrySet().stream()
                 .max(Map.Entry.comparingByValue()) // Find the entry with the highest count
                .map(Map.Entry::getKey) // Get the subject name (the key)
                 .orElse("N/A"); // Default if no subjects found
    }

    String peakDayOfWeek = completedAppointments.stream()
            .collect(Collectors.groupingBy(app -> app.getAppointmentTime().getDayOfWeek(), Collectors.counting())) // Group by DayOfWeek, count
            .entrySet().stream()
            .max(Map.Entry.comparingByValue()) // Find day with max count
             .map(entry -> entry.getKey().toString()) // Get the DayOfWeek name as String
            .orElse("N/A"); // Default if no peak day found

    analytics.put("totalCompletedWeek", totalCompletedWeek);
    analytics.put("totalCompletedMonth", totalCompletedMonth);
    analytics.put("averageDurationMinutes", averageDurationMinutes);
    analytics.put("mostFrequentSubject", mostFrequentSubject);
    analytics.put("peakDayOfWeek", peakDayOfWeek);

    System.out.println("Calculated analytics for " + username + ": " + analytics); // Debugging
    return analytics;
}

    /**
     * Retrieves a specific Appointment by its unique ID.
     * Performs a direct lookup in the main appointments map.
     *
     * @param appointmentId The ID of the appointment to retrieve.
     * @return The Appointment object if found, otherwise null.
     */
    public Appointment getAppointmentById(int appointmentId) {
        if (appointmentId <= 0) {
             System.err.println("WARN: getAppointmentById called with invalid ID: " + appointmentId);
             return null;
        }
        Appointment app = appointments.get(appointmentId);
        if (app == null) {
             System.out.println("INFO: getAppointmentById did not find an appointment with ID: " + appointmentId);
         }
        return app;
    }

} // End of ConsultationController class