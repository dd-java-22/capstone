/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Abstraction for location search operations. Implementations may use Android Geocoder, Google
 * Places SDK, or any other geocoding backend.
 */
public interface LocationSearchProvider {

  /**
   * Searches for locations matching the given query text (e.g. an address or place name).
   *
   * @param query the user-entered search text.
   * @return a future that resolves to a list of matching locations, possibly empty.
   */
  CompletableFuture<List<PickedLocation>> search(String query);

  /**
   * Performs a reverse lookup from coordinates to a human-readable location.
   *
   * @param latitude  the latitude of the point to look up.
   * @param longitude the longitude of the point to look up.
   * @return a future that resolves to a list of candidate locations for the coordinates.
   */
  CompletableFuture<List<PickedLocation>> reverseGeocode(double latitude, double longitude);
}
