/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package controllers;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.cache.NinjaCache;
import ninja.params.PathParam;
import processor.GrabAirAsiaService;


@Singleton
public class ApplicationController {

  final static Logger logger = LoggerFactory.getLogger("com.filavents.grabflight");

  @Inject
  NinjaCache ninjaCache;

  @Inject
  GrabAirAsiaService grabFlightService;
  
  public Result index() {

    Result result = Results.html();

    return result;
  }

  public Result indexWithData(@PathParam("flight") String flight, @PathParam("destFrom") String destFrom,
      @PathParam("destTo") String destTo, @PathParam("dateFrom") String dateFrom,
      @PathParam("dateTo") String dateTo) {

    Result result = Results.html();
    result.render("flight", flight.toLowerCase());
    result.render("destFrom", destFrom.toUpperCase());
    result.render("destTo", destTo.toUpperCase());
    result.render("dateFrom", dateFrom);
    result.render("dateTo", dateTo);

    return result;
  }

  public Result getFlightMonthly(@PathParam("destFrom") String destFrom,
      @PathParam("destTo") String destTo, @PathParam("dateFrom") String dateFrom,
      @PathParam("dateTo") String dateTo, @PathParam("flight") String flight, Context ctx)
      throws IOException, JSONException {
    
    Result result = Results.text();
    JSONObject resp = new JSONObject();
    
    // Verify API caller origin
    /*if (!ctx.getHostname().equalsIgnoreCase("localhost:8080")
        && !ctx.getHostname().equalsIgnoreCase("grabflight.filavents.com")) {
      logger.error(ctx.getRequestPath() + " - not authorized api call from: " + ctx.getHostname());
      resp.put("error", "Not authorized. Please contact alifaziz@gmail.com if you insist");
      result.status(500);
      result.render(resp);
      return result;
    }*/

    if (flight.equalsIgnoreCase("airasia")) {

      String cacheResponse = (String) ninjaCache.get(destFrom + destTo + dateFrom + dateTo);
      if (null == cacheResponse) {

        JSONObject flightResults =
            grabFlightService.getSchedulesbyMonthRange(destFrom, destTo, dateFrom, dateTo);

        // if got schedules then only cache, otherwise dont!
        if (flightResults.getJSONArray("schedules").length() > 0
            || flightResults.getJSONArray("schedules").getJSONArray(0).length() > 0
            || flightResults.getJSONArray("schedules").getJSONArray(1).length() > 0) {
          ninjaCache.set(destFrom + destTo + dateFrom + dateTo, flightResults.toString());
        }

        logger.debug(ctx.getRequestPath() + " - " + flightResults.toString());

        result.render(flightResults);

      } else {

        result.render(cacheResponse);

      }
    } else {
      logger.error(ctx.getRequestPath() + " - flight type not recognized");
      resp.put("error", "flight type not recognized");
      result.status(500);
      result.render(resp);
    }

    return result;
  }

  public Result helloWorldJson() {
    SimplePojo simplePojo = new SimplePojo();
    simplePojo.content = "Hello World! Hello Json!";
    return Results.json().render(simplePojo);
  }

  public static class SimplePojo {
    public String content;
  }
}
