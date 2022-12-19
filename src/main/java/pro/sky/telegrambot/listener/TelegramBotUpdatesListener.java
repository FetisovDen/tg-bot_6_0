package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationTaskService notificationTaskService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                if (update.message().text().equals("/start")) {
                    logger.info("Бот поприветсвовал нового пользователя");
                    telegramBot.execute(new SendMessage(update.message().chat().id(),
                            "Привет, " + update.message().from().firstName()
                                    + ". Я умею делать напоминания, формат ввода - dd.MM.yyyy HH:mm текст напоминания" +
                                    ", пример - 18.12.2022 20:49 Полить цветы ")
                    );
                } else {
                    logger.info("Отправлено на обработку напоминания");
                    notificationTaskService.saveNotificationTask(update);
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        } catch (NullPointerException n) {
            logger.error("NullPointerException(), среди Updates есть null. Скорее всего в chat().id() - юзер, который оставил напоминание, покинул бота.");
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
    }
}


