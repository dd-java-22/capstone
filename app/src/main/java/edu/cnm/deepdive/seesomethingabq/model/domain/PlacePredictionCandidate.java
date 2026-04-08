/*
 * Copyright 2026 CNM Ingenuity, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.model.domain;

import androidx.annotation.NonNull;

/**
 * Lightweight UI model representing a Google Places autocomplete prediction. This is not a final
 * selected location; it only contains the data needed to display a prediction row and later fetch
 * the full place details.
 */
public class PlacePredictionCandidate {

  private final String placeId;
  private final String displayText;

  /**
   * Creates a prediction candidate.
   *
   * @param placeId Google Places place ID.
   * @param displayText user-visible display text.
   */
  public PlacePredictionCandidate(@NonNull String placeId, @NonNull String displayText) {
    this.placeId = placeId;
    this.displayText = displayText;
  }

  /**
   * Returns the Google Places place ID for this prediction.
   *
   * @return place ID.
   */
  @NonNull
  public String getPlaceId() {
    return placeId;
  }

  /**
   * Returns the user-visible display text for this prediction.
   *
   * @return display text.
   */
  @NonNull
  public String getDisplayText() {
    return displayText;
  }
}
