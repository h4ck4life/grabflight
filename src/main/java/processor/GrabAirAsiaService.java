package processor;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public interface GrabAirAsiaService {

  /**
   * Get flight schedules by month range
   * 
   * @param destFrom Flight origin
   * @param destTo Flight destination
   * @param dateFrom Depart date
   * @param dateTo Return date
   * @return {@link JSONObject} List of schedules + fees
   * @throws IOException
   * @throws JSONException
   */
  public JSONObject getSchedulesbyMonthRange(String destFrom, String destTo, String dateFrom,
      String dateTo) throws IOException, JSONException;

}
