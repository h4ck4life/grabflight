package processor.impl;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.inject.Inject;

import ninja.utils.NinjaProperties;
import processor.GrabFlightService;

public class GrabAirAsiaServiceImpl implements GrabFlightService {

  @Inject
  NinjaProperties ninjaProperties;

  public static void main(String[] args) throws IOException, JSONException {

  }

  @Override
  public ArrayList<ArrayList<JSONObject>> getSchedulesbyMonthRange(String destFrom, String destTo,
      String dateFrom, String dateTo) throws IOException, JSONException {

    String airAsiaScrapUrlMonthly = String.format(ninjaProperties.get("flight.scrap.airasia"), destFrom, destTo, dateFrom, dateTo);
    String airAsiaBaseURL = ninjaProperties.get("flight.baseurl.airasia");

    Document doc = Jsoup.connect(airAsiaScrapUrlMonthly)
        .userAgent(
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
        .timeout(10000).get();


    ArrayList<ArrayList<JSONObject>> flightDatas = new ArrayList<>();
    ArrayList<ArrayList<JSONObject>> flightTrips = new ArrayList<>();
    ArrayList<JSONObject> flightSchedules = new ArrayList<>();

    // Get departure & return scehdule + fares
    Elements departReturnTableEls = doc.select("table.low-fare-cal-month");
    for (Element tripsEls : departReturnTableEls) {

      // Get scheduleTimeRow
      Elements scheduleTimeRowEls = tripsEls.select("div.low-fare-cal-day");
      for (Element scheduleTimeRow : scheduleTimeRowEls) {

        JSONObject flightSchedule = new JSONObject();

        String date = "";
        String price = "";
        String linkToPurchase = "";

        // Get date
        Elements dateRows = scheduleTimeRow.select("[type=radio]");
        if (dateRows.size() > 0) {
          date = dateRows.select("[type=radio]").get(0).attr("value");
        } else {
          // date = "No Flights";
        }

        // Get price
        Elements priceRows = scheduleTimeRow.select("div.low-fare-cal-day-amount");
        if (priceRows.size() > 0) {
          price = priceRows.get(0).text().replaceAll(" MYR", "");
        } else {
          // price = "N/A";
        }

        // Get link to purchase
        Elements linkToPurchaseRows = scheduleTimeRow.select("a[href]");
        if (linkToPurchaseRows.size() > 0) {
          linkToPurchase = linkToPurchaseRows.get(0).attr("href");
        } else {
          // linkToPurchase = "N/A";
        }

        if (!date.equals("") && !price.equals("") && !linkToPurchase.equals("")) {

          flightSchedule.put("date", date);
          flightSchedule.put("price", price);
          flightSchedule.put("linkToPurchase", airAsiaBaseURL + linkToPurchase);

          flightSchedules.add(flightSchedule);
        }

      }
      
      flightTrips.add(flightSchedules);

    }

    // Add all JSONObjects to root
    flightDatas.addAll(flightTrips);
    
    //System.out.println(flightDatas.toString());

    return flightDatas;
  }

}
