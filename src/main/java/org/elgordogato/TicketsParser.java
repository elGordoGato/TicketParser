package org.elgordogato;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TicketsParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Пожалуйста, укажите имя файла tickets.json в качестве аргумента.");
            return;
        }
        final String sourceFile = args[0];
        final String resultFileName = "result.txt";
        String departure = "Vladivostok";
        String arrival = "Tel Aviv";

        List<Ticket> tickets = getTickets(sourceFile).stream()
                .filter(ticket -> (ticket.getDepartureCity().equals(departure)
                        && ticket.getArrivalCity().equals(arrival)))
                .collect(Collectors.toList());
        Map<String, Duration> minFlightTime = findMinFlightTime(tickets);
        Double medianAvgDiff = findMedianAvgDiff(tickets);
        writeToFile(resultFileName, departure, arrival, minFlightTime, medianAvgDiff);
    }

    private static Map<String, Duration> findMinFlightTime(List<Ticket> tickets) {
        Map<String, Duration> minFlightTime = new HashMap<>();
        for (Ticket ticket : tickets) {
            String carrier = ticket.getCarrier();
            Instant departureTime = LocalDateTime.parse(
                            ticket.getDepartureTime(), FORMATTER)
                    .atZone(ZoneOffset.ofHours(ticket.getDepartureTimeZone()))
                    .toInstant();
            Instant arrivalTime = LocalDateTime.parse(
                            ticket.getArrivalTime(), FORMATTER)
                    .atZone(ZoneOffset.ofHours(ticket.getArrivalTimeZone()))
                    .toInstant();
            Duration duration = Duration.between(arrivalTime, departureTime);
            if (!minFlightTime.containsKey(carrier) || duration.compareTo(minFlightTime.get(carrier)) > 0) {
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
        if (tickets.size() % 2 == 1) {
            return tickets.get(middle).getPrice();
        }
        return (tickets.get(middle - 1).getPrice() + tickets.get(middle).getPrice()) / 2;
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

    private static void writeToFile(String resultFileName, String departure, String arrival,
                                    Map<String, Duration> minFlightTime, Double medianAvgDiff) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(resultFileName))) {
            writer.println(String.format(
                    "Минимальное время полета между городами %s и %s для каждого авиаперевозчика:",
                    departure, arrival));
            for (Map.Entry<String, Duration> entry : minFlightTime.entrySet()) {
                writer.println(entry.getKey() + ": " + formatDuration(entry.getValue()));
            }
            writer.println(String.format(
                    "Разница между средней ценой и медианой для полета между городами %s и %s:",
                    departure, arrival));
            writer.println(String.format("%.2f", medianAvgDiff) + " руб.");
        } catch (IOException e) {
            System.out.println("Произошла ошибка при записи в файл:");
            e.printStackTrace();
        }
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long remainingMinutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, remainingMinutes);
    }
}
