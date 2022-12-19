package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;
    @Autowired
    private TelegramBot telegramBot;


    public void saveNotificationTask(Update update) {
        try {
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
            Matcher matcher = pattern.matcher(update.message().text());
            if (matcher.matches()) {
                LocalDateTime date = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                String text = matcher.group(3);
                if (date.isAfter(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                    logger.info("Напоминание сохранено");
                    notificationTaskRepository.save(new NotificationTask(update.message().chat().id(), text, date));
                    telegramBot.execute(new SendMessage(update.message().chat().id(), "Сообщение сохранено"));
                } else {
                    logger.info("Дата и время находятся в прошлом, сообщение не сохранено");
                    telegramBot.execute(new SendMessage(
                            update.message().chat().id(),
                            "Вы пытаетесь заглянуть в прошлое. " +
                                    "Актуальные дата и время: " + (LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))));
                }
            } else {
                logger.info("Некорректный формат ввода напоминания" + update.message().text());
                telegramBot.execute(new SendMessage(update.message().chat().id(),
                        "Некорректный формат ввода напоминания." +
                                " Пример корректного- 18.12.2022 20:49 Полить цветы"));
            }
        } catch (DateTimeParseException d) {
            logger.error("Некорректный ввод формата даты, либо времени" + update.message().text());
            telegramBot.execute(new SendMessage(update.message().chat().id(),
                    "Некорректный ввод даты, либо времени"));
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void searchCurrentReminders() {
        logger.info("Поиск в бд записей с напоминаниями по текущему времени");
        List<NotificationTask> listNotificationTaskToBeSentNow = notificationTaskRepository
                .findNotificationTaskByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        for (NotificationTask notificationTask : listNotificationTaskToBeSentNow) {
            if (notificationTask.getDateTime().equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                logger.info("Отправка напоминаний");
                telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getTextMassage()));
                logger.info("Очищение бд от напоминаний, которые отправлены");
                notificationTaskRepository.delete(notificationTask);
            }
        }
    }
}
