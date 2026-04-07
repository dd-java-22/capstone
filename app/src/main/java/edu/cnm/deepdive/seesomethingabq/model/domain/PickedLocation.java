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
package edu.cnm.deepdive.seesomethingabq.model.domain;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

/**
 * Neutral result model for the location picker. Carries the display text and coordinates of a
 * confirmed location selection. Implements {@link Parcelable} so it can be passed between fragments
 * via the Fragment Result API.
 */
public class PickedLocation implements Parcelable {

  private final String displayText;
  private final double latitude;
  private final double longitude;

  public PickedLocation(@NonNull String displayText, double latitude, double longitude) {
    this.displayText = displayText;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  protected PickedLocation(Parcel in) {
    displayText = in.readString();
    latitude = in.readDouble();
    longitude = in.readDouble();
  }

  public static final Creator<PickedLocation> CREATOR = new Creator<>() {
    @Override
    public PickedLocation createFromParcel(Parcel in) {
      return new PickedLocation(in);
    }

    @Override
    public PickedLocation[] newArray(int size) {
      return new PickedLocation[size];
    }
  };

  @NonNull
  public String getDisplayText() {
    return displayText;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeString(displayText);
    dest.writeDouble(latitude);
    dest.writeDouble(longitude);
  }
}
