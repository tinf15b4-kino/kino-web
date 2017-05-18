package de.tinf15b4.kino.data.reminders;

import org.springframework.data.jpa.repository.JpaRepository;

import de.tinf15b4.kino.data.reminders.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
}
