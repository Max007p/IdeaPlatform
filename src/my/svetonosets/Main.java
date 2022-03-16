package my.svetonosets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        List<Long> listOfFlightTime = getListOfFlightTimeFromTickets(args[0]);
        LocalTime averageFlightTime = getAverageFlightTime(listOfFlightTime);
        LocalTime percentileOfFlightTime = getPercentileFlightTime(90, listOfFlightTime);
        System.out.println("Average flight time = " +
                averageFlightTime + " \n90th percentile of flight time = " +
                percentileOfFlightTime
        );
    }

    private static List<Long> getListOfFlightTimeFromTickets(String fileName){
        List<Long> listOfFlightTime = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try(FileReader fileReader = new FileReader(fileName)) {
            JSONObject tickets = (JSONObject) jsonParser.parse(fileReader);
            JSONArray ticketsList = (JSONArray) tickets.get("tickets");
            for (Object ticket : ticketsList){
                Duration time = getFlightTimeFromTicket((JSONObject) ticket);
                listOfFlightTime.add(time.toMinutes());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return listOfFlightTime;
    }

    private static LocalTime getAverageFlightTime(List<Long> listOfFlightTime){
        long sumOfFlightTime = listOfFlightTime.stream().mapToLong(t -> t).sum();
        return LocalTime.of(
                (int)(sumOfFlightTime / listOfFlightTime.size()) / 60,
                (int)(sumOfFlightTime / listOfFlightTime.size()) % 60
        );
    }

    private static String checkTimeFormat(String time){
        if (time.length() < 5)
            time = 0 + time;
        return time;
    }

    private static Duration getFlightTimeFromTicket(JSONObject ticket){
        String departureTime = (String) ticket.get("departure_time");
        String arrivalTime = (String) ticket.get("arrival_time");
        LocalTime time1 = LocalTime.parse(checkTimeFormat(departureTime));
        LocalTime time2 = LocalTime.parse(checkTimeFormat(arrivalTime));
        return Duration.between(time1,time2);
    }

    private static LocalTime getPercentileFlightTime(int percentile,List<Long> listOfFlightTime){
        List<Long> listOfFlightTimeSorted = listOfFlightTime.stream().sorted().collect(Collectors.toList());
        double k = listOfFlightTime.size() * percentile/100;
        return LocalTime.of(
                listOfFlightTimeSorted.get((int)Math.ceil(k)).intValue() / 60,
                listOfFlightTimeSorted.get((int)Math.ceil(k)).intValue() % 60
        );
    }


}
