package ru.netology.diplom.test;


import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.diplom.data.*;
import ru.netology.diplom.pages.StartPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.diplom.data.SQLHelper.*;
import static ru.netology.diplom.pages.PurchasePage.*;

public class PaymentTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void start() {
        open("http://localhost:8080");
    }

    @AfterEach
    void teardown() {
        cleanData();
    }

    @Test 
    void fillFormFirstCard() {
        var paymentPage = StartPage.getPaymentPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var correctCardData = DateGenerator.getLowerBorder();
        var successNotification = paymentPage.getSuccessNotificationOfTransaction();
        paymentPage.fillThePaymentForm(firstCardData.getCardNumber(),
                correctCardData.getCardMonth(), correctCardData.getCardYear(),
                firstCardData.getCardOwner(), firstCardData.getCvcCode());
        successNotification.shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Успешно " + "Операция одобрена Банком."));
        assertEquals("APPROVED", getCardStatusPayment());
    }

    @Test
    void fillFormSecondCard() {
        var paymentPage = StartPage.getPaymentPage();
        var secondCardData = DataGenerator.getSecondCardData("en");
        var correctCardData = DateGenerator.getLowerBorder();
        var successNotification = paymentPage.getSuccessNotificationOfTransaction();
        paymentPage.fillThePaymentForm(secondCardData.getCardNumber(),
                correctCardData.getCardMonth(), correctCardData.getCardYear(),
                secondCardData.getCardOwner(), secondCardData.getCvcCode());
        successNotification.shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Отказ " + "Операция отклонена Банком."));
        assertEquals("DECLINED", getCardStatusPayment());
    }

    @Test
    void errorWhenFormIsEmpty() {
        var paymentPage = StartPage.getPaymentPage();
        var errorInCardNumber = paymentPage.getErrorInCardNumberField();
        var errorInMonth = paymentPage.getErrorInMonthField();
        var errorInYear = paymentPage.getErrorInYearField();
        var errorInOwner = paymentPage.getErrorInOwnerField();
        var errorInCVC = paymentPage.getErrorInCVCCodeField();
        sendEmptyPaymentForm();
        errorInCardNumber.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInMonth.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInYear.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInOwner.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInCVC.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void errorWhenFifteenCardNumber() {
        var paymentPage = StartPage.getPaymentPage();
        var rightCardData = DataGenerator.getRightDataCard("en");
        var wrongCardData = DataGenerator.getWrongDataCard("ru");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorInCardData = paymentPage.getErrorInCardNumberField();
        paymentPage.fillThePaymentForm(wrongCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), rightCardData.getCardOwner(), rightCardData.getCvcCode());
        errorInCardData.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void fillValidCardNumber() {
        var paymentPage = StartPage.getPaymentPage();
        var rightCardData = DataGenerator.getRightDataCard("en");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorNotification = paymentPage.getErrorNotificationOfTransaction();
        var resultValueInCardNumber = paymentPage.getCardNumberField();
        paymentPage.fillThePaymentForm(rightCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), rightCardData.getCardOwner(), rightCardData.getCvcCode());
        assertEquals("1111 2222 3333 4444", resultValueInCardNumber.getValue());
        errorNotification.shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Ошибка " + "Ошибка! Банк отказал в проведении операции."));
    }

    @Test
    void fillUpperBorderDate() {
        var paymentPage = StartPage.getPaymentPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var lowerBorderCardDate = DateGenerator.getUpperBorder();
        var successNotification = paymentPage.getSuccessNotificationOfTransaction();
        paymentPage.fillThePaymentForm(firstCardData.getCardNumber(),
                lowerBorderCardDate.getCardMonth(), lowerBorderCardDate.getCardYear(),
                firstCardData.getCardOwner(), firstCardData.getCvcCode());
        successNotification.shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Успешно " + "Операция одобрена Банком."));
        assertEquals("APPROVED", getCardStatusPayment());
    }

    @Test
    void fillPreviousMonth() {
        var paymentPage = StartPage.getPaymentPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var previousCardDate = DateGenerator.getPreviousMonth();
        var errorNotification = paymentPage.getErrorInMonthField();
        paymentPage.fillThePaymentForm(firstCardData.getCardNumber(),
                previousCardDate.getCardMonth(), previousCardDate.getCardYear(),
                firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Истёк срок действия карты"));
    }

    @Test
    void fillPreviousYear() {
        var paymentPage = StartPage.getPaymentPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var previousCardDate = DateGenerator.getPreviousYear();
        var errorNotification = paymentPage.getErrorInYearField();
        paymentPage.fillThePaymentForm(correctCardData.getCardNumber(),
                previousCardDate.getCardMonth(), previousCardDate.getCardYear(),
                correctCardData.getCardOwner(), correctCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Истёк срок действия карты"));
    }

    @Test
    void fillOverPeriod() {
        var paymentPage = StartPage.getPaymentPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var overPeriodCardDate = DateGenerator.getOverDate();
        var errorNotification = paymentPage.getErrorInYearField();
        paymentPage.fillThePaymentForm(firstCardData.getCardNumber(),
                overPeriodCardDate.getCardMonth(), overPeriodCardDate.getCardYear(),
                firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    @Test
    void errorWhenMonthIsNull(){
        var paymentPage = StartPage.getPaymentPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var nextYearsDate = DateGenerator.getUpperBorder();
        var errorNotification = paymentPage.getErrorInMonthField();
        paymentPage.fillThePaymentForm(correctCardData.getCardNumber(), "00", nextYearsDate.getCardYear(),
                correctCardData.getCardOwner(), correctCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    @Test
    void errorWhenWrongMonth(){
        var paymentPage = StartPage.getPaymentPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var currentDate = DateGenerator.getLowerBorder();
        var errorNotification = paymentPage.getErrorInMonthField();
        paymentPage.fillThePaymentForm(correctCardData.getCardNumber(), "13", currentDate.getCardYear(),
                correctCardData.getCardOwner(), correctCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    @Test
    void errorWhenWrongFormatMonth() {
        var paymentPage = StartPage.getPaymentPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var currentDate = DateGenerator.getLowerBorder();
        var wrongDateValueFormat = DateGenerator.wrongInputDateFormat();
        var errorNotification = paymentPage.getErrorInMonthField();
        paymentPage.fillThePaymentForm(firstCardData.getCardNumber(),
                wrongDateValueFormat, currentDate.getCardYear(), firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorWhenWrongFormatYear() {
        var paymentPage = StartPage.getPaymentPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var currentDate = DateGenerator.getLowerBorder();
        var wrongDateValueFormat = DateGenerator.wrongInputDateFormat();
        var errorNotification = paymentPage.getErrorInYearField();
        paymentPage.fillThePaymentForm(firstCardData.getCardNumber(),
                currentDate.getCardMonth(), wrongDateValueFormat, firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorWhenWrongCyrillicOwner() {
        var paymentPage = StartPage.getPaymentPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var wrongCardData = DataGenerator.getWrongDataCard("ru");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorOwner = paymentPage.getErrorInOwnerField();
        paymentPage.fillThePaymentForm(correctCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), wrongCardData.getCardOwner(),
                correctCardData.getCvcCode());
        errorOwner.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorWhenInvalidOwner() {
        var paymentPage = StartPage.getPaymentPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var cardPeriod = DateGenerator.getLowerBorder();
        var specSymbolsInOwnerField = DataGenerator.getSpecSymbolsInOwnerField();
        var errorOwner = paymentPage.getErrorInOwnerField();
        paymentPage.fillThePaymentForm(correctCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), correctCardData.getCardOwner() + specSymbolsInOwnerField,
                correctCardData.getCvcCode());
        errorOwner.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorWhenInvalidCVC() {
        var paymentPage = StartPage.getPaymentPage();
        var rightCardData = DataGenerator.getRightDataCard("en");
        var wrongCardData = DataGenerator.getWrongDataCard("ru");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorInCVCCode = paymentPage.getErrorInCVCCodeField();
        paymentPage.fillThePaymentForm(rightCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), rightCardData.getCardOwner(), wrongCardData.getCvcCode());
        errorInCVCCode.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void cannotFillFieldsWithInvalidData() {
        var paymentPage = StartPage.getPaymentPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var specSymbols = DataGenerator.getSpecSymbols();
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorNotification = paymentPage.getErrorNotificationOfTransaction();
        paymentPage.fillThePaymentForm(correctCardData.getCardNumber(), specSymbols + cardPeriod.getCardMonth(),
                specSymbols + cardPeriod.getCardYear(), correctCardData.getCardOwner(),
                specSymbols + correctCardData.getCvcCode());
        errorNotification.shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Ошибка " + "Ошибка! Банк отказал в проведении операции."));
    }

    @Test
    void cannotFillFieldsWithOverData() {
        var paymentPage = StartPage.getPaymentPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorNotification = paymentPage.getErrorNotificationOfTransaction();
        paymentPage.fillThePaymentForm(correctCardData.getCardNumber(), cardPeriod.getCardMonth() + "1",
                cardPeriod.getCardYear() + "1", correctCardData.getCardOwner(),
                correctCardData.getCvcCode() + "1");
        errorNotification.shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Ошибка " + "Ошибка! Банк отказал в проведении операции."));
    }
}