package com.consultation.util;

import com.consultation.controller.ConsultationController;
import com.consultation.model.TimeSlot;
import com.consultation.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class DataInitializer {

    public static void initializeData(ConsultationController controller) {
        System.out.println("Starting Data Initialization...");

        // --- Initialize Professors ---
        // Pass the primary subject during registration
        User profCapule = controller.registerUser("prof.capule", "pass123", "PROFESSOR",
            "Prof. Janice Capule", "jcapule@tip.edu.ph", "Operating Systems"); // Subject passed
        if (profCapule != null) {
            // Add any additional subjects AFTER registration
            profCapule.addSubject("Data Structures and Algorithms");
        }

        User profBarcelos = controller.registerUser("prof.barcelos", "pass123", "PROFESSOR", "Prof. Elsa Barcelos",
             "ebarcelos@tip.edu.ph", "General Education Elective"); // Subject passed
        // if (profBarcelos != null) profBarcelos.addSubject(...); // No need if only one subject from init

        User profCabautan = controller.registerUser("prof.cabautan", "pass123", "PROFESSOR", "Prof. Rosmina Joy Cabautan",
             "rcabautan@tip.edu.ph", "Information Management"); // Subject passed
        // if (profCabautan != null) ...

        User profCueto = controller.registerUser("prof.cueto", "pass123", "PROFESSOR", "Prof. Ruel Cueto",
            "rcueto@tip.edu.ph", "Discrete Mathematics"); // Subject passed
        // if (profCueto != null) ...

        User profManrique = controller.registerUser("prof.manrique", "pass123", "PROFESSOR", "Prof. Marcel Manrique",
            "mmanrique@tip.edu.ph", "Introduction to Numerical Analysis"); // Subject passed
        // if (profManrique != null) ...

        User profMontera = controller.registerUser("prof.montera", "pass123", "PROFESSOR", "Prof. Efraem Montera",
            "emontera@tip.edu.ph", "Physical Education 4"); // Subject passed
        // if (profMontera != null) ...

        User profLim = controller.registerUser("prof.lim", "pass123", "PROFESSOR", "Prof. Daniel Lim",
            "dlim@tip.edu.ph", "Web Development"); // Subject passed
        // if (profLim != null) ...


        // --- Initialize Counselors ---
        // Pass null for the subject argument for non-professors
        User counselorGarcia = controller.registerUser("counselor.garcia", "pass123", "COUNSELOR", "Ms. Sofia Garcia",
            "sgarcia@tip.edu.ph", null); // Pass null for subject

        // --- Initialize Students ---
        // Pass null for the subject argument for non-professors
        User student1 = controller.registerUser("student1", "pass123", "STUDENT", "Juan Cruz",
            "jcruz@tip.edu.ph", null); // Pass null for subject
        if (student1 != null) {
            // Add student enrollments AFTER registration
            student1.addSubject("Operating Systems");
            student1.addSubject("Data Structures and Algorithms");
            student1.addSubject("General Education Elective");
            student1.addSubject("Information Management");
            student1.addSubject("Discrete Mathematics");
            student1.addSubject("Introduction to Numerical Analysis");
            student1.addSubject("Physical Education 4");
            student1.addSubject("Web Development");
        }

        // Pass null for the subject argument for non-professors
        User student2 = controller.registerUser("student2", "pass123", "STUDENT", "Cindy Lopez",
            "clopez@tip.edu.ph", null); // Pass null for subject
        if (student2 != null) {
            // Add student enrollments AFTER registration
            student2.addSubject("Operating Systems");
            student2.addSubject("Data Structures and Algorithms");
            student2.addSubject("General Education Elective");
            student2.addSubject("Information Management");
            student2.addSubject("Discrete Mathematics");
            student2.addSubject("Introduction to Numerical Analysis");
            student2.addSubject("Physical Education 4");
            student2.addSubject("Web Development");
        }
<<<<<<< HEAD
        
     // Pass null for the subject argument for non-professors
        User student3 = controller.registerUser("student3", "pass123", "STUDENT", "Kenneth Beliganio",
            "clopez@tip.edu.ph", null); // Pass null for subject
        if (student2 != null) {
            // Add student enrollments AFTER registration
            student2.addSubject("Operating Systems");
            student2.addSubject("Data Structures and Algorithms");
            student2.addSubject("General Education Elective");
            student2.addSubject("Information Management");
            student2.addSubject("Discrete Mathematics");
            student2.addSubject("Introduction to Numerical Analysis");
            student2.addSubject("Physical Education 4");
            student2.addSubject("Web Development");
        }
        
     // Pass null for the subject argument for non-professors
        User student4 = controller.registerUser("student4", "pass123", "STUDENT", "Luis Certeza",
            "clopez@tip.edu.ph", null); // Pass null for subject
        if (student2 != null) {
            // Add student enrollments AFTER registration
            student2.addSubject("Operating Systems");
            student2.addSubject("Data Structures and Algorithms");
            student2.addSubject("General Education Elective");
            student2.addSubject("Information Management");
            student2.addSubject("Discrete Mathematics");
            student2.addSubject("Introduction to Numerical Analysis");
            student2.addSubject("Physical Education 4");
            student2.addSubject("Web Development");
        }
        
     // Pass null for the subject argument for non-professors
        User student5 = controller.registerUser("student5", "pass123", "STUDENT", "Marc Igama",
            "clopez@tip.edu.ph", null); // Pass null for subject
        if (student2 != null) {
            // Add student enrollments AFTER registration
            student2.addSubject("Operating Systems");
            student2.addSubject("Data Structures and Algorithms");
            student2.addSubject("General Education Elective");
            student2.addSubject("Information Management");
            student2.addSubject("Discrete Mathematics");
            student2.addSubject("Introduction to Numerical Analysis");
            student2.addSubject("Physical Education 4");
            student2.addSubject("Web Development");
        }
=======
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832

        // --- Initialize DEFAULT Time Slots for ALL Staff ---
        // This part is removed because registerUser now calls initializeDefaultSlotsForUser
        // System.out.println("\nInitializing default slots for all staff...");
        // controller.initializeAllTimeSlots(); <-- REMOVE THIS CALL
        // System.out.println("Default slots initialized.");
        // The slots are initialized individually within registerUser for staff roles.


        // --- START: Customize Staff Availability ---
        // (Keep this section as it was)
        System.out.println("\nCustomizing staff availability...");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);

<<<<<<< HEAD
        if (profCapule != null) {
            markSlotUnavailable(controller, profCapule.getUsername(), tomorrow, LocalTime.of(9, 0));
            markSlotUnavailable(controller, profCapule.getUsername(), tomorrow, LocalTime.of(10, 0));
        }
        if (counselorGarcia != null) {
            List<TimeSlot> slotsToMakeUnavailable = controller.getTimeSlotsForDate(counselorGarcia.getUsername(), dayAfterTomorrow);
            if (slotsToMakeUnavailable != null && !slotsToMakeUnavailable.isEmpty()) {
                 System.out.println("  Making Counselor Garcia unavailable on " + dayAfterTomorrow + "...");
                for (TimeSlot slot : slotsToMakeUnavailable) {
                    slot.setMarkedAvailable(false);
                     System.out.println("    Marked slot " + slot.getStartTime() + "-" + slot.getEndTime() + " as unavailable.");
                }
            } else {
                 System.out.println("  No default slots found for Counselor Garcia on " + dayAfterTomorrow + " to mark unavailable.");
             }
        }
        if (profLim != null) {
             markSlotUnavailable(controller, profLim.getUsername(), today, LocalTime.of(13, 0));
        }
=======
        // Remove the code that marks slots as unavailable
        // if (profCapule != null) {
        //     markSlotUnavailable(controller, profCapule.getUsername(), tomorrow, LocalTime.of(9, 0));
        //     markSlotUnavailable(controller, profCapule.getUsername(), tomorrow, LocalTime.of(10, 0));
        // }
        // if (counselorGarcia != null) {
        //     List<TimeSlot> slotsToMakeUnavailable = controller.getTimeSlotsForDate(counselorGarcia.getUsername(), dayAfterTomorrow);
        //     if (slotsToMakeUnavailable != null && !slotsToMakeUnavailable.isEmpty()) {
        //          System.out.println("  Making Counselor Garcia unavailable on " + dayAfterTomorrow + "...");
        //         for (TimeSlot slot : slotsToMakeUnavailable) {
        //             slot.setMarkedAvailable(false);
        //              System.out.println("    Marked slot " + slot.getStartTime() + "-" + slot.getEndTime() + " as unavailable.");
        //         }
        //     } else {
        //          System.out.println("  No default slots found for Counselor Garcia on " + dayAfterTomorrow + " to mark unavailable.");
        //      }
        // }
        // if (profLim != null) {
        //      markSlotUnavailable(controller, profLim.getUsername(), today, LocalTime.of(13, 0));
        // }
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
        LocalTime lateAfternoon = LocalTime.of(17, 0);
        LocalTime evening = LocalTime.of(18, 0);
        if (profLim != null) {
           controller.addTimeSlot(profLim, today, lateAfternoon, evening);
        }
        System.out.println("Staff availability customization complete.");
        // --- END: Customize Staff Availability ---

<<<<<<< HEAD

        // --- Create Sample Appointments ---
        System.out.println("\nCreating sample appointments...");

        if (student1 != null && profCapule != null) controller.createAppointment(student1, profCapule, "Operating Systems", 30);
        if (student2 != null && profCapule != null) controller.createAppointment(student2, profCapule, "Data Structures and Algorithms", 30);
        if (student1 != null && profBarcelos != null) controller.createAppointment(student1, profBarcelos, "General Education Elective", 30);
        if (student2 != null && profCabautan != null) controller.createAppointment(student2, profCabautan, "Information Management", 30);
        if (student1 != null && profCueto != null) controller.createAppointment(student1, profCueto, "Discrete Mathematics", 30);
        if (student2 != null && profManrique != null) controller.createAppointment(student2, profManrique, "Introduction to Numerical Analysis", 30);
        if (student1 != null && profMontera != null) controller.createAppointment(student1, profMontera, "Physical Education 4", 30);
        if (student2 != null && profLim != null) controller.createAppointment(student2, profLim, "Web Development", 30);
        if (student1 != null && counselorGarcia != null) controller.createAppointment(student1, counselorGarcia, "Academic Advising", 45);

=======
        // Remove sample appointments section
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
        System.out.println("\nInitialization complete.");
        if (student1 != null) {
            System.out.println("Total appointments for student1: " + controller.getUserAppointments(student1).size());
        }
        if (profCapule != null){
             System.out.println("Subjects for prof.capule: " + profCapule.getSubjects());
             List<TimeSlot> profCapuleTomorrowSlots = controller.getTimeSlotsForDate(profCapule.getUsername(), tomorrow);
             long unavailableCount = profCapuleTomorrowSlots.stream().filter(ts -> !ts.isAvailable()).count();
             System.out.println("Prof Capule Availability Check for " + tomorrow + ": Found " + profCapuleTomorrowSlots.size() + " total slots, " + unavailableCount + " are unavailable/booked.");
        }
         if (counselorGarcia != null) {
              List<TimeSlot> garciaDATTomorrowSlots = controller.getTimeSlotsForDate(counselorGarcia.getUsername(), dayAfterTomorrow);
              long garciaUnavailableCount = garciaDATTomorrowSlots.stream().filter(ts -> !ts.isMarkedAvailable).count(); // Check marked unavailable
              System.out.println("Counselor Garcia Availability Check for " + dayAfterTomorrow + ": Found " + garciaDATTomorrowSlots.size() + " total slots, " + garciaUnavailableCount + " are marked unavailable.");
          }
    }

    /**
     * Helper method to find a specific TimeSlot and mark it as unavailable.
     */
    private static void markSlotUnavailable(ConsultationController controller, String username, LocalDate date, LocalTime startTime) {
<<<<<<< HEAD
        List<TimeSlot> slots = controller.getTimeSlotsForDate(username, date);
        Optional<TimeSlot> slotToMark = slots.stream()
                .filter(slot -> slot.getStartTime().equals(startTime))
                .findFirst();

        if (slotToMark.isPresent()) {
            TimeSlot slot = slotToMark.get();
            if (slot.isBooked()) {
                System.out.println("  INFO: Marking slot " + date + " " + startTime + " for " + username + " as unavailable, but it is already booked by " + (slot.getAppointment() != null ? slot.getAppointment().getStudent().getUsername() : "Unknown") + ".");
            }
            slot.setMarkedAvailable(false); // Mark the slot as not available for booking in general
            System.out.println("  Marked slot " + date + " " + startTime + " for " + username + " as unavailable.");
        } else {
            System.out.println("  WARN: Could not find slot starting at " + startTime + " on " + date + " for user " + username + " to mark unavailable.");
        }
=======
        // Don't mark any slots as unavailable
        System.out.println("  INFO: Skipping marking slot as unavailable to ensure all slots are available.");
        return;
>>>>>>> 97d91e1730b3f848456d6926936e586d2409b832
    }
}