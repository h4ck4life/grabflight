package processor.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ninja.utils.NinjaProperties;
import processor.GrabAirAsiaService;

public class GrabAirAsiaServiceImpl implements GrabAirAsiaService {
  
  final static Logger logger = LoggerFactory.getLogger("com.filavents.grabflight");

  @Inject
  NinjaProperties ninjaProperties;

  public static void main(String[] args) throws IOException, JSONException {

  }

  @Override
  public JSONObject getSchedulesbyMonthRange(String destFrom, String destTo, String dateFrom,
      String dateTo) throws IOException, JSONException {

    String airAsiaScrapUrlMonthly = String.format(ninjaProperties.get("flight.scrap.airasia"),
        destFrom, destTo, dateFrom, dateTo);
    String airAsiaBaseURL = ninjaProperties.get("flight.baseurl.airasia");
    
    //System.out.println(airAsiaScrapUrlMonthly);

    Document doc = Jsoup.connect(airAsiaScrapUrlMonthly)
        .userAgent(
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 "
                + (new BigInteger(130, new SecureRandom()).toString(32)))
        .timeout(120000).get();


    JSONObject flightDatasRoot = new JSONObject();
    ArrayList<JSONObject> flightDatas = new ArrayList<>();
    ArrayList<JSONObject> flightTrips = new ArrayList<>();
    ArrayList<JSONObject> flightSchedules = new ArrayList<>();

    // Get departure & return scehdule + fares
    Elements departReturnTableEls = doc.select("table.low-fare-cal-month");
    for (Element tripsEls : departReturnTableEls) {

      // Get scheduleTimeRow
      Elements scheduleTimeRowEls = tripsEls.select("div.low-fare-cal-day");
      
      if (scheduleTimeRowEls.size() > 0) {
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

          if (!date.equals("") && !price.equals("")) {
            flightSchedule.put("date", date);
            flightSchedule.put("price", price);
            if (!linkToPurchase.equals("")) {
              flightSchedule.put("linkToPurchase", airAsiaBaseURL + linkToPurchase);
            }
            flightSchedules.add(flightSchedule);
          }

        }

        JSONObject flightScheduleRoot = new JSONObject();
        if (tripsEls.parent().elementSiblingIndex() == 1) {
          flightScheduleRoot.put("departure", flightSchedules);
        }
        if (tripsEls.parent().elementSiblingIndex() == 2) {
          flightScheduleRoot.put("return", flightSchedules);
        }
        flightTrips.add(flightScheduleRoot);
        
        // clear flight schedules!
        flightSchedules.clear();

      }

    }

    // Add all JSONObjects to root
    flightDatas.addAll(flightTrips);

    // System.out.println(flightDatas.toString());

    flightDatasRoot.put("locationFrom", destFrom);
    flightDatasRoot.put("LocationTo", destTo);
    flightDatasRoot.put("dateFrom", dateFrom);
    flightDatasRoot.put("dateTo", dateTo);
    flightDatasRoot.put("schedules", flightDatas);
    
    return flightDatasRoot;
  }

}
