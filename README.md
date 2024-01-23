# TicketsParser

Программа на языке программирования
java, которая прочитает файл tickets.json и
рассчитает:
- Минимальное время полета между городами
  Владивосток и Тель-Авив для каждого
  авиаперевозчика
- Разницу между средней ценой и медианой для
  полета между городами  Владивосток и Тель-Авив
  Программа должна вызываться из командной строки
  Linux, результаты должны быть представлены в
  текстовом виде.
  В качестве результата нужно прислать ответы на
  поставленные вопросы и ссылку на исходный код.


Программа читает данные из файла указанного в аргументах командной строки (`resources/tickets.json`). 

Результаты выводятся в консоль и записываются в файл `resources/output.txt`.

---

## Стек:
> Java 17, Maven, Jackson, Lombok

---

## Запуск приложения:

1) Склонировать репозиторий 
    > `git clone https://github.com/elGordoGato/TicketParser.git`
   
2) Перейти в корневую директорию проекта
    > `cd TicketParser/`

3) Находясь в корневой папке собрать проект 
    > `mvn clean package`
4) Запустить сгенерированный jar-файл
    > `java -jar target/TicketParser-1.0-SNAPSHOT.jar resources/tickets.json`

## Ответы:

Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика:
- SU: 06:00
- S7: 06:30
- TK: 05:50
- BA: 08:05

Разница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив:
- 460.00 руб.