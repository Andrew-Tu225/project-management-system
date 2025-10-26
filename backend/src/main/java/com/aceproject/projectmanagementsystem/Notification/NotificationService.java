package com.aceproject.projectmanagementsystem.Notification;

import com.aceproject.projectmanagementsystem.task.Task;
import com.aceproject.projectmanagementsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate template;
    @Autowired
    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate template) {
        this.notificationRepository = notificationRepository;
        this.template = template;
    }

    public Notification createNotification(String receiverEmail, String message){
        Notification notification = new Notification();
        notification.setReceiverEmail(receiverEmail);
        notification.setMessage(message);
        return notificationRepository.save(notification);
    }

    public void sendTaskDueSoonNotification(Task task){
        String notificationMessage = "This is a reminder that you have a task "
                + task.getTitle() + " with importance level " + task.getImportance()+ " due in two days";
        for(User person:task.getPeople()){
            Notification notification = createNotification(person.getEmail(), notificationMessage);
            template.convertAndSendToUser(person.getEmail(), "/queue/notification", notification);
        }
    }
}
