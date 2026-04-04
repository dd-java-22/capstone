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

import android.location.Location;
import java.util.concurrent.CompletableFuture;

/**
 * Abstraction for obtaining the device's current location. Implementations may use
 * FusedLocationProviderClient or any other location source. Kept separate from
 * {@link LocationSearchProvider} because current-location lookup involves permissions and hardware,
 * while search/geocoding is a pure data operation.
 */
public interface CurrentLocationProvider {

  /**
   * Requests the device's current location.
   *
   * @return a future that resolves to the current {@link Location}, or fails if location is
   *     unavailable or permission was denied.
   */
  CompletableFuture<Location> getCurrentLocation();
}
