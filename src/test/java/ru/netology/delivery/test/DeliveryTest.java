package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    @BeforeAll
    public static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and plan meeting")
    void shouldSuccesfulPlanMeeting() {

        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $("[data-test-id=city] input").setValue(validUser.getCity());                                            //заполнение поля город
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);                                     //очистка поля дата
        $("[data-test-id=date] input").setValue(firstMeetingDate);                                              //заполнение поля дата
        $("[data-test-id=name] input").setValue(validUser.getName());                                            //заполнение поля имя
        $("[data-test-id=phone] input").setValue(validUser.getPhone());                                          //заполнение поля телефон
        $("[data-test-id=agreement]").click();                                                                  //заполнение чек бокса
        $(".button").shouldHave(Condition.text("Запланировать")).click();                                       //нажатие кнопки "Запланировать"
        $("[data-test-id=success-notification]").shouldBe(Condition.visible, Duration.ofSeconds(15))            //проверка popup
                .shouldHave(Condition.exactText("Успешно! Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);                                     //очистка поля дата
        $("[data-test-id=date] input").setValue(secondMeetingDate);                                             //заполнение поля дата
        $(".button").shouldHave(Condition.text("Запланировать")).click();                                       //нажатие кнопки "Запланировать"
        $("[data-test-id=replan-notification]").shouldBe(Condition.visible)                                     //проверка popup
                .shouldHave(Condition.text("Необходимо подтверждение" +
                        " У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $("[data-test-id=replan-notification] .button").shouldHave(Condition.text("Перепланировать")).click();  //нажатие кнопки "перепланировать"
        $("[data-test-id=success-notification]").shouldBe(Condition.visible)                                    //проверка popup
                .shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + secondMeetingDate))
                .shouldBe(Condition.visible);
    }
}
