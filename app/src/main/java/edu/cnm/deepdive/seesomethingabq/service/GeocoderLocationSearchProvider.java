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

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * {@link LocationSearchProvider} implementation backed by the Android {@link Geocoder}. All
 * blocking geocoder calls run on a single background thread. This class can be replaced with a
 * Google Places SDK implementation without changing the calling code.
 */
@Singleton
public class GeocoderLocationSearchProvider implements LocationSearchProvider {

  private static final int MAX_RESULTS = 5;

  private final Geocoder geocoder;
  private final ExecutorService executor;

  @Inject
  GeocoderLocationSearchProvider(@ApplicationContext Context context) {
    this.geocoder = new Geocoder(context);
    this.executor = Executors.newSingleThreadExecutor();
  }

  @SuppressWarnings("deprecation")
  @Override
  public CompletableFuture<List<PickedLocation>> search(String query) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        List<Address> addresses = geocoder.getFromLocationName(query, MAX_RESULTS);
        if (addresses == null) {
          return Collections.emptyList();
        }
        return toPickedLocations(addresses);
      } catch (IOException e) {
        throw new CompletionException(e);
      }
    }, executor);
  }

  @SuppressWarnings("deprecation")
  @Override
  public CompletableFuture<List<PickedLocation>> reverseGeocode(double latitude, double longitude) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, MAX_RESULTS);
        if (addresses == null) {
          return Collections.emptyList();
        }
        return toPickedLocations(addresses);
      } catch (IOException e) {
        throw new CompletionException(e);
      }
    }, executor);
  }

  private List<PickedLocation> toPickedLocations(List<Address> addresses) {
    return addresses.stream()
        .filter(a -> a.hasLatitude() && a.hasLongitude())
        .map(this::toPickedLocation)
        .collect(Collectors.toList());
  }

  private PickedLocation toPickedLocation(Address address) {
    String displayText = buildDisplayText(address);
    return new PickedLocation(displayText, address.getLatitude(), address.getLongitude());
  }

  private String buildDisplayText(Address address) {
    int lines = address.getMaxAddressLineIndex();
    if (lines >= 0) {
      return IntStream.rangeClosed(0, lines)
          .mapToObj(address::getAddressLine)
          .collect(Collectors.joining(", "));
    }
    StringBuilder sb = new StringBuilder();
    if (address.getThoroughfare() != null) {
      sb.append(address.getThoroughfare());
    }
    if (address.getLocality() != null) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(address.getLocality());
    }
    if (address.getAdminArea() != null) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(address.getAdminArea());
    }
    return sb.length() > 0 ? sb.toString() : String.format("%.5f, %.5f",
        address.getLatitude(), address.getLongitude());
  }
}
