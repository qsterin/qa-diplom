 **Для запуска автотестов:**
 1. Установить IntelliJ IDEA, Docker desktop
 2. с помощью команды docker pull в терминале загрузите с DockerHub следующие образы:
      * mysql:8.0;
      * postgres:12-alpine;
      * node:latest.
 3. Установить плагин Docker в IDEA.
 4. Открыть проект в IntelliJ IDEA:
 5. Установить плагин Lombok в IDEA.
 6. На проверочном устройстве должна быть выставлена Автоматическая установка даты и времени.
 7. Запустить Docker desktop.
 8. В IDEA открыть Terminal:
    * в первой вкладке терминала ввести команду: docker-compose up;
    * во второй вкладке терминала ввести команду: java -jar artifacts/aqa-shop.jar; 
 9. Запустить тесты.