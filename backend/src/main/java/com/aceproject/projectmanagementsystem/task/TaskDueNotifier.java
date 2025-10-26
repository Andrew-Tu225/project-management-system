package com.aceproject.projectmanagementsystem.task;

import com.aceproject.projectmanagementsystem.Notification.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class TaskDueNotifier {
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Autowired
    public TaskDueNotifier(TaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    private List<Task> getTasksDueInDates(Date dueDate){
        return taskRepository.findDueTasksByDueDate(dueDate);
    }

    @Transactional
    @Scheduled(cron = "0 0 22 * * *")
    public void sendTaskDueTwoDaysNotification(){
        LocalDate localTwoDaysLater = LocalDate.now().plusDays(2);
        Date twoDaysLater = Date.from(localTwoDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println(twoDaysLater);
        List<Task> taksDueInTwoDays = getTasksDueInDates(twoDaysLater);
        for (Task task: taksDueInTwoDays){
            notificationService.sendTaskDueSoonNotification(task);
        }
    }
}
