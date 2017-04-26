package de.tinf15b4.kino.data.reminders;

import org.springframework.stereotype.Service;

import de.tinf15b4.kino.data.ServiceImplModel;
import de.tinf15b4.kino.data.reminders.Reminder;

@Service
public class ReminderServiceImpl extends ServiceImplModel<Reminder, ReminderRepository> implements ReminderService {

}
