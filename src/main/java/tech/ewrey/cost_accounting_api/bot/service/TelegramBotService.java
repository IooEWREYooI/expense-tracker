package tech.ewrey.cost_accounting_api.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import tech.ewrey.cost_accounting_api.entity.Cost;
import tech.ewrey.cost_accounting_api.entity.Settings;
import tech.ewrey.cost_accounting_api.jpa.CostRepository;
import tech.ewrey.cost_accounting_api.jpa.SettingsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.api.bot.token}")
    private String BOT_TOKEN;

    @Value("${telegram.api.bot.username}")
    private String BOT_USERNAME;

    @Autowired
    private MessageEnProperties messageEnProperties;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private CostRepository costRepository;

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi TelegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        TelegramBotsApi.registerBot(this);
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChat().getId();
            String messageText = update.getMessage().getText();
            log.info("Message: from {} text \"{}\"", update.getMessage().getFrom().getUserName(), messageText);

            if (messageText.equals("/start")) {
                sendMessage(update, messageEnProperties.getProperty("start.msg"));
            } else if (messageText.startsWith("/limit ")) {
                int number = Integer.parseInt(messageText.split("/limit ")[1]);

                var listSettingsByChatId = settingsRepository.findAllByChatId(chatId);
                settingsRepository.deleteAll(listSettingsByChatId);
                Settings settings = new Settings(number, chatId);
                settingsRepository.save(settings);

                sendMessage(update, messageEnProperties.getProperty("limit.msg").formatted(number));
            } else if (messageText.split(" ").length == 2 || messageText.startsWith("/cost ")) {
                var updatedMessageText = messageText.replaceAll("/cost ", "");
                String costName = updatedMessageText.split(" ")[0];
                int costAmount = Integer.parseInt(updatedMessageText.split(" ")[1]);

                Cost cost = new Cost();
                cost.setChatId(chatId);
                cost.setName(costName);
                cost.setAmount(costAmount);

                costRepository.save(cost);

                var costs = costRepository.findAllByChatId(chatId)
                        .stream()
                        .filter(x -> x.getTime().getDayOfYear() == LocalDate.now().getDayOfYear())
                        .map(Cost::getAmount)
                        .reduce(Integer::sum).get();

                var limit = settingsRepository.findAllByChatId(chatId).get(0).getLimit();

                BigDecimal percents = new BigDecimal(Double.toString(((double) costs / limit) * 100));

                sendMessage(update, messageEnProperties.getProperty("cost.msg").formatted(costs,
                        percents.setScale(2, RoundingMode.HALF_UP).toString()));
            } else if (messageText.equals("/costs")) {
                var costs = costRepository.findAllByChatId(chatId)
                        .stream()
                        .filter(x -> x.getTime().getDayOfYear() == LocalDate.now().getDayOfYear())
                        .map(Cost::toString)
                        .collect(Collectors.joining("\n"));

                sendMessage(update, costs);
            }
        }
    }

    private void sendMessage(Update update, String text) {
        try {
            log.info("Send Message to {} with text : \"{}\"", update.getMessage().getFrom().getUserName(), text);
            execute(
                    SendMessage.builder()
                            .chatId(update.getMessage().getChatId())
                            .parseMode("Markdown")
                            .text(text)
                            .build());
        }
        catch (TelegramApiException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }


}
