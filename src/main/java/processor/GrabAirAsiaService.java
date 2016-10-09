package processor;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public interface GrabAirAsiaService {

  public ArrayList<ArrayList<JSONObject>> getSchedulesbyMonthRange(String destFrom, String destTo,
      String dateFrom, String dateTo) throws IOException, JSONException;

}
