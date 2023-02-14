package ru.netology.diplom.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.diplom.data.DateGenerator;
import ru.netology.diplom.data.DataGenerator;
import ru.netology.diplom.pages.StartPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.diplom.data.SQLHelper.*;
import static ru.netology.diplom.pages.PurchasePage.sendEmptyCreditForm;


public class CreditTest {
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
    void fillTheFormFirstCard() {
        var creditPage = StartPage.getCreditPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var correctCardData = DateGenerator.getLowerBorder();
        var successNotification = creditPage.getSuccessNotificationOfTransaction();
        creditPage.fillTheCreditForm(firstCardData.getCardNumber(),
                correctCardData.getCardMonth(), correctCardData.getCardYear(),
                firstCardData.getCardOwner(), firstCardData.getCvcCode());
        successNotification.shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(exactText("Успешно " + "Операция одобрена Банком."));
        assertEquals("APPROVED", getCardStatusCredit());
    }

    @Test
    void fillTheFormSecondCard() {
        var creditPage = StartPage.getCreditPage();
        var secondCardData = DataGenerator.getSecondCardData("en");
        var correctCardData = DateGenerator.getLowerBorder();
        var successNotification = creditPage.getSuccessNotificationOfTransaction();
        creditPage.fillTheCreditForm(secondCardData.getCardNumber(),
                correctCardData.getCardMonth(), correctCardData.getCardYear(),
                secondCardData.getCardOwner(), secondCardData.getCvcCode());
        successNotification.shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Отказ " + "Операция отклонена Банком."));
        assertEquals("DECLINED", getCardStatusCredit());
    }

    @Test
    void errorWhenFormIsEmpty() {
        var creditPage = StartPage.getCreditPage();
        var errorInCardNumber = creditPage.getErrorInCardNumberField();
        var errorInMonth = creditPage.getErrorInMonthField();
        var errorInYear = creditPage.getErrorInYearField();
        var errorInOwner = creditPage.getErrorInOwnerField();
        var errorInCVC = creditPage.getErrorInCVCCodeField();
        sendEmptyCreditForm();
        errorInCardNumber.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInMonth.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInYear.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInOwner.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
        errorInCVC.shouldBe(visible).shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void errorWhenFifteenNumbersCardNumber() {
        var creditPage = StartPage.getCreditPage();
        var rightCardData = DataGenerator.getRightDataCard("en");
        var wrongCardData = DataGenerator.getWrongDataCard("ru");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorInCardData = creditPage.getErrorInCardNumberField();
        creditPage.fillTheCreditForm(wrongCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), rightCardData.getCardOwner(), rightCardData.getCvcCode());
        errorInCardData.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }
    @Test 
    void fillCardNumberWithValidValues() {
        var creditPage = StartPage.getCreditPage();
        var rightCardData = DataGenerator.getRightDataCard("en");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorNotification = creditPage.getErrorNotificationOfTransaction();
        var resultValueInCardNumber = creditPage.getCardNumberField();
        creditPage.fillTheCreditForm(rightCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), rightCardData.getCardOwner(), rightCardData.getCvcCode());
        assertEquals("1111 2222 3333 4444", resultValueInCardNumber.getValue());
        errorNotification.shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Ошибка " + "Ошибка! Банк отказал в проведении операции."));
    }

    @Test
    void fillUpperBorderDate() {
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var lowerBorderCardDate = DateGenerator.getUpperBorder();
        var errorNotification = creditPage.getErrorNotificationOfTransaction();
        creditPage.fillTheCreditForm( correctCardData.getCardNumber(),
                lowerBorderCardDate.getCardMonth(), lowerBorderCardDate.getCardYear(),
                correctCardData.getCardOwner(),  correctCardData.getCvcCode());
        errorNotification.shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Ошибка " + "Ошибка! Банк отказал в проведении операции."));
    }

    @Test
    void fillPreviousMonthDate() {
        var creditPage = StartPage.getCreditPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var previousCardDate = DateGenerator.getPreviousMonth();
        var errorNotification = creditPage.getErrorInMonthField();
        creditPage.fillTheCreditForm(firstCardData.getCardNumber(),
                previousCardDate.getCardMonth(), previousCardDate.getCardYear(),
                firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Истёк срок действия карты"));
    }

    @Test
    void fillPreviousYearDate() {
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var previousCardDate = DateGenerator.getPreviousYear();
        var errorNotification = creditPage.getErrorInYearField();
        creditPage.fillTheCreditForm(correctCardData.getCardNumber(),
                previousCardDate.getCardMonth(), previousCardDate.getCardYear(),
                correctCardData.getCardOwner(), correctCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Истёк срок действия карты"));
    }

    @Test
    void fillOverPeriodDate() {
        var creditPage = StartPage.getCreditPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var overPeriodCardDate = DateGenerator.getOverDate();
        var errorNotification = creditPage.getErrorInYearField();
        creditPage.fillTheCreditForm(firstCardData.getCardNumber(),
                overPeriodCardDate.getCardMonth(), overPeriodCardDate.getCardYear(),
                firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    @Test
    void errorWhenMonthIsNull(){
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var nextYearsDate = DateGenerator.getUpperBorder();
        var errorNotification = creditPage.getErrorInMonthField();
        creditPage.fillTheCreditForm(correctCardData.getCardNumber(), "00", nextYearsDate.getCardYear(),
                correctCardData.getCardOwner(), correctCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    @Test
    void errorWhenEnterThirteenthMonth(){
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var currentDate = DateGenerator.getLowerBorder();
        var errorNotification = creditPage.getErrorInMonthField();
        creditPage.fillTheCreditForm(correctCardData.getCardNumber(), "13", currentDate.getCardYear(),
                correctCardData.getCardOwner(), correctCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверно указан срок действия карты"));
    }

    @Test
    void errorWhenWrongFormatMonth() {
        var creditPage = StartPage.getCreditPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var currentDate = DateGenerator.getLowerBorder();
        var wrongDateValueFormat = DateGenerator.wrongInputDateFormat();
        var errorNotification = creditPage.getErrorInMonthField();
        creditPage.fillThePaymentForm(firstCardData.getCardNumber(),
                wrongDateValueFormat, currentDate.getCardYear(), firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorWhenWrongFormatYear() {
        var creditPage = StartPage.getCreditPage();
        var firstCardData = DataGenerator.getFirstCardData("en");
        var currentDate = DateGenerator.getLowerBorder();
        var wrongDateValueFormat = DateGenerator.wrongInputDateFormat();
        var errorNotification = creditPage.getErrorInYearField();
        creditPage.fillTheCreditForm(firstCardData.getCardNumber(),
                currentDate.getCardMonth(), wrongDateValueFormat, firstCardData.getCardOwner(), firstCardData.getCvcCode());
        errorNotification.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorWhenInvalidCyrillicOwner() {
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var wrongCardData = DataGenerator.getWrongDataCard("ru");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorOwner = creditPage.getErrorInOwnerField();
        creditPage.fillTheCreditForm(correctCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), wrongCardData.getCardOwner(),
                correctCardData.getCvcCode());
        errorOwner.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorIfInvalidOwner() {
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var cardPeriod = DateGenerator.getLowerBorder();
        var specSymbols = DataGenerator.getSpecSymbolsInOwnerField();
        var errorOwner = creditPage.getErrorInOwnerField();
        creditPage.fillTheCreditForm(correctCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), correctCardData.getCardOwner() + specSymbols,
                correctCardData.getCvcCode());
        errorOwner.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void errorIfWrongFormatCVC() {
        var creditPage = StartPage.getCreditPage();
        var rightCardData = DataGenerator.getRightDataCard("en");
        var wrongCardData = DataGenerator.getWrongDataCard("ru");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorInCVCCode = creditPage.getErrorInCVCCodeField();
        creditPage.fillTheCreditForm(rightCardData.getCardNumber(), cardPeriod.getCardMonth(),
                cardPeriod.getCardYear(), rightCardData.getCardOwner(), wrongCardData.getCvcCode());
        errorInCVCCode.shouldBe(visible).shouldHave(exactText("Неверный формат"));
    }

    @Test
    void cannotFillWithInvalidValues() {
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var cardPeriod = DateGenerator.getLowerBorder();
        var specSymbols = DataGenerator.getSpecSymbols();
        var errorNotification = creditPage.getErrorNotificationOfTransaction();
        creditPage.fillTheCreditForm(correctCardData.getCardNumber(), specSymbols + cardPeriod.getCardMonth(),
                specSymbols + cardPeriod.getCardYear(), correctCardData.getCardOwner(),
                specSymbols + correctCardData.getCvcCode());
        errorNotification.shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Ошибка " + "Ошибка! Банк отказал в проведении операции."));
    }

    @Test
    void cannotFillFieldsWithValuesMoreThenEnough() {
        var creditPage = StartPage.getCreditPage();
        var correctCardData = DataGenerator.getRightDataCard("en");
        var cardPeriod = DateGenerator.getLowerBorder();
        var errorNotification = creditPage.getErrorNotificationOfTransaction();
        creditPage.fillTheCreditForm(correctCardData.getCardNumber(), cardPeriod.getCardMonth() + "1",
                cardPeriod.getCardYear() + "1", correctCardData.getCardOwner(),
                correctCardData.getCvcCode() + "1");
        errorNotification.shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Ошибка " + "Ошибка! Банк отказал в проведении операции."));
    }
}