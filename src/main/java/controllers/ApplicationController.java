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
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import processor.GrabAirAsiaService;


@Singleton
public class ApplicationController {

  @Inject
  GrabAirAsiaService grabFlightService;

  public Result index() {
    return Results.html();
  }

  public Result getFlightMonthly(@PathParam("destFrom") String destFrom,
      @PathParam("destTo") String destTo, @PathParam("dateFrom") String dateFrom,
      @PathParam("dateTo") String dateTo, @PathParam("flight") String flight)
      throws IOException, JSONException {

    Result result = Results.text();
    JSONObject resp = new JSONObject();

    if (flight.equalsIgnoreCase("airasia")) {
      JSONObject flightResults =
          grabFlightService.getSchedulesbyMonthRange(destFrom, destTo, dateFrom, dateTo);
      result.render(flightResults);
    } else {
      resp.put("error", "flight type not recognized");
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
