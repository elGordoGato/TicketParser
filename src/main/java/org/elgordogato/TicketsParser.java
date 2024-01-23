package org.elgordogato;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketsParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Пожалуйста, укажите имя файла tickets.json в качестве аргумента.");
            return;
        }
        final String sourceFile = args[0];
        final String outputName = "resources/output.txt";
        String departure = "Владивосток";
        String arrival = "Тель-Авив";

        List<Ticket> tickets = getTickets(sourceFile).stream()
                .filter(ticket -> (ticket.getOrigin_name().equals(departure)
                        && ticket.getDestination_name().equals(arrival)))
                .collect(Collectors.toList());
        Map<String, Duration> minFlightTime = findMinFlightTime(tickets);
        Double medianAvgDiff = findMedianAvgDiff(tickets);
        writeToFile(outputName, departure, arrival, minFlightTime, medianAvgDiff);
    }

    private static Map<String, Duration> findMinFlightTime(List<Ticket> tickets) {
        Map<String, Duration> minFlightTime = new HashMap<>();
        for (Ticket ticket : tickets) {
            String carrier = ticket.getCarrier();
            LocalDateTime departureTime = LocalDateTime.parse(
                    String.format("%s %s", ticket.getDeparture_date(), ticket.getDeparture_time()),
                    FORMATTER);
            LocalDateTime arrivalTime = LocalDateTime.parse(
                    String.format("%s %s", ticket.getArrival_date(), ticket.getArrival_time()),
                    FORMATTER);
            Duration duration = Duration.between(departureTime, arrivalTime);
            if (!minFlightTime.containsKey(carrier) || duration.compareTo(minFlightTime.get(carrier)) < 0) {
                minFlightTime.put(carrier, duration);
            }
        }
        return minFlightTime;
    }

    private static Double findMedianAvgDiff(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            return 0.0;
        }
        tickets.sort(Comparator.comparingDouble(Ticket::getPrice));
        int middle = tickets.size() / 2;
        double avgPrice = tickets.stream().mapToDouble(Ticket::getPrice).sum() / tickets.size();
        double medianPrice;
        if (tickets.size() % 2 == 1) {
            medianPrice = tickets.get(middle).getPrice();
        } else medianPrice = (tickets.get(middle - 1).getPrice() + tickets.get(middle).getPrice()) / 2.0;
        return avgPrice - medianPrice;
    }


    private static List<Ticket> getTickets(String sourceFile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (mapper.readValue(
                    new File(sourceFile),
                    new TypeReference<Map<String, List<Ticket>>>() {
                    })).get("tickets");
        } catch (IOException e) {
            throw new RuntimeException("Произошла ошибка при чтении или обработке файла tickets.json:");
        }
    }

    private static void writeToFile(String outputName, String departure, String arrival,
                                    Map<String, Duration> minFlightTime, Double medianAvgDiff) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputName))) {
            output(String.format(
                    "Минимальное время полета между городами %s и %s для каждого авиаперевозчика:",
                    departure, arrival), writer);
            for (Map.Entry<String, Duration> entry : minFlightTime.entrySet()) {
                output(entry.getKey() + ": " + formatDuration(entry.getValue()), writer);
            }
            output(String.format(
                    "Разница между средней ценой и медианой для полета между городами %s и %s:",
                    departure, arrival), writer);
            output(String.format("%.2f", medianAvgDiff) + " руб.", writer);
        } catch (IOException e) {
            System.out.println("Произошла ошибка при записи в файл:");
            e.printStackTrace();
        }
    }

    private static void output(String msg, PrintWriter writer) {
        System.out.println(msg);
        writer.println(msg);
    }


    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long remainingMinutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, remainingMinutes);
    }
}
