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

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import dagger.hilt.android.qualifiers.ApplicationContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;

/**
 * {@link CurrentLocationProvider} implementation that uses the Android {@link LocationManager} API.
 * Requires {@code ACCESS_FINE_LOCATION} or {@code ACCESS_COARSE_LOCATION} permission to be granted
 * before calling {@link #getCurrentLocation()}.
 */
@Singleton
public class LocationManagerCurrentLocationProvider implements CurrentLocationProvider {

  private static final long MAX_LAST_KNOWN_AGE_MS = 60_000;

  private final LocationManager locationManager;
  private final Context context;

  @Inject
  LocationManagerCurrentLocationProvider(@ApplicationContext Context context) {
    this.context = context;
    this.locationManager =
        (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  @SuppressLint("MissingPermission")
  @Override
  public CompletableFuture<Location> getCurrentLocation() {
    CompletableFuture<Location> future = new CompletableFuture<>();
    try {
      Location last = getRecentLastKnown();
      if (last != null) {
        future.complete(last);
        return future;
      }
      String provider = pickProvider();
      if (provider == null) {
        future.completeExceptionally(
            new IllegalStateException("No location provider is enabled"));
        return future;
      }
      locationManager.getCurrentLocation(provider, null, context.getMainExecutor(),
          location -> {
            if (location != null) {
              future.complete(location);
            } else {
              future.completeExceptionally(
                  new IllegalStateException("Location unavailable"));
            }
          });
    } catch (SecurityException e) {
      future.completeExceptionally(e);
    }
    return future;
  }

  @SuppressLint("MissingPermission")
  private Location getRecentLastKnown() {
    Location gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    Location network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    Location best = pickMoreRecent(gps, network);
    if (best != null && System.currentTimeMillis() - best.getTime() <= MAX_LAST_KNOWN_AGE_MS) {
      return best;
    }
    return null;
  }

  private Location pickMoreRecent(Location a, Location b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    return a.getTime() >= b.getTime() ? a : b;
  }

  private String pickProvider() {
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      return LocationManager.GPS_PROVIDER;
    }
    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
      return LocationManager.NETWORK_PROVIDER;
    }
    return null;
  }
}
