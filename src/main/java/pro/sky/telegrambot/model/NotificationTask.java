package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "notification_task")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String textMassage;
    private LocalDateTime dateTime;

    public NotificationTask(Long chatId, String textMassage, LocalDateTime dateAndTime) {
        this.chatId = chatId;
        this.textMassage = textMassage;
        this.dateTime = dateAndTime;
    }

    public NotificationTask() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTextMassage() {
        return textMassage;
    }

    public void setTextMassage(String textMassage) {
        this.textMassage = textMassage;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return getId().equals(that.getId()) && getChatId().equals(that.getChatId()) && getTextMassage().equals(that.getTextMassage()) && getDateTime().equals(that.getDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getChatId(), getTextMassage(), getDateTime());
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                ", textMassage='" + textMassage + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
