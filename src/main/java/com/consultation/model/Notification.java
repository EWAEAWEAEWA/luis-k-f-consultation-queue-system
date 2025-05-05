package com.consultation.model;

import java.time.LocalDateTime;
import java.util.Objects; // Import for Objects.hash
import java.util.UUID;    // Import for generating unique IDs

/**
 * Represents a notification message for a user within the consultation system.
 * Each notification has a unique ID, timestamp, message content, and a read status.
 */
public class Notification {

    /**
     * Unique identifier for the notification.
     */
    private final String id;

    /**
     * The date and time when the notification was generated.
     */
    private final LocalDateTime timestamp;

    /**
     * The content of the notification message.
     */
    private final String message;

    /**
     * Flag indicating whether the notification has been read by the user.
     * Defaults to false (unread).
     */
    private boolean isRead;

    /**
     * Constructs a new Notification object.
     * Automatically generates a unique ID and sets the read status to false.
     *
     * @param timestamp The time the notification was created.
     * @param message   The notification message content.
     */
    public Notification(LocalDateTime timestamp, String message) {
        // Generate a unique identifier for this notification instance
        this.id = UUID.randomUUID().toString();
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.isRead = false; // Notifications are initially unread
    }

    /**
     * Gets the unique ID of this notification.
     *
     * @return The unique identifier string.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the timestamp when this notification was created.
     *
     * @return The LocalDateTime of creation.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the message content of this notification.
     *
     * @return The notification message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Checks if this notification has been marked as read.
     *
     * @return true if the notification is read, false otherwise.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of this notification.
     *
     * @param read true to mark as read, false to mark as unread.
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Notifications are considered equal if they have the same unique ID.
     *
     * @param o The reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Identity check
        if (o == null || getClass() != o.getClass()) return false; // Null and type check
        Notification that = (Notification) o;
        return Objects.equals(id, that.id); // Equality based on unique ID
    }

    /**
     * Returns a hash code value for the object.
     * Based on the unique ID to be consistent with equals().
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash code based on unique ID
    }

    /**
     * Returns a string representation of the notification object.
     * Useful for debugging.
     *
     * @return A string representation including ID, timestamp, read status, and message.
     */
    @Override
    public String toString() {
        return "Notification{" +
               "id='" + id + '\'' +
               ", timestamp=" + timestamp +
               ", message='" + message + '\'' +
               ", isRead=" + isRead +
               '}';
    }
}