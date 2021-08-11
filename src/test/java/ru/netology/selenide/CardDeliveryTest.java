package ru.netology.selenide;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
    }

    @Test
    public void shouldSubmitRequest() {
        SelenideElement form = $("[method=post]");
        form.$("[data-test-id=city] input").setValue("Астрахань");
        form.$("[data-test-id=date] input").doubleClick();
        form.$("[data-test-id=date] input").sendKeys(Keys.DELETE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        form.$("[data-test-id=date] input").setValue(new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime()));
        form.$("[data-test-id=name] input").setValue("Васин-Красин Василь");
        form.$("[data-test-id=phone] input").setValue("+71234567890");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$("[role=button] .button__content").click();
        $("[data-test-id=notification] .notification__content").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText("Встреча успешно забронирована на " + new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime())));
    }


    @Test
    public void shouldSubmitRequestByClick() {
        SelenideElement form = $("[method=post]");
        form.$("[data-test-id=city] input").setValue("ас");
        $$("[class=popup__content] .menu-item__control").get(1).shouldHave(exactText("Астрахань")).click();

        form.$("[data-test-id=date] input").click(); //кликаем по иконке
        String currentDay = $("[class=calendar__layout] .calendar__day_state_current.calendar__day").text(); //получаем ближайшую возможную дату брони
        String lastDay = "zero"; //устанавливаем значение крайнему дню
        int size = $$("[class=calendar__layout] .calendar__row .calendar__day").size(); // получаем размер(кол-во ячеек) всплывающего компонента - календаря
        for (int i = 0; i < 7; i++) { //создаем цикл, в котором перебираем значения до момента получения пустоты. Тем самым определим крайнее значение месяца, после которого кликаем на стрелку
            lastDay = $$("[class=calendar__layout] .calendar__row .calendar__day").get(size - 1 - i).text();
            if (!lastDay.equals("")) {
                break;
            }
        }
        int difference = Integer.parseInt(lastDay) - Integer.parseInt(currentDay);
        if (difference < 4){
            $$(".calendar__arrow_direction_right").get(1).click();
            $(byText(String.valueOf(4-difference))).shouldHave(cssClass("calendar__day")).click();
        }else{
            int tmp =Integer.parseInt(currentDay)+4;
            $(byText(String.valueOf(tmp))).shouldHave(cssClass("calendar__day")).click();
        }
        form.$("[data-test-id=name] input").setValue("Филимонов Илья");
        form.$("[data-test-id=phone] input").setValue("+71234567890");
        form.$("[data-test-id=agreement] .checkbox__box").click();
        form.$("[role=button] .button__content").click();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        $("[data-test-id=notification] .notification__content").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText("Встреча успешно забронирована на " + new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime())));
    }
}
