package com.sysco.perso.analytics.client.statemachine;

import com.sysco.perso.analytics.client.statemachine.data.TrackerData;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface OfferTrackerClient {
  boolean startTracker(List<TrackerData> objects);
}
