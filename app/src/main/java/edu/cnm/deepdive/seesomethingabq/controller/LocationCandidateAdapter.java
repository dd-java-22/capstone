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
package edu.cnm.deepdive.seesomethingabq.controller;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import java.util.Collections;
import java.util.List;

/**
 * RecyclerView adapter that displays a list of {@link PickedLocation} candidates for the user to
 * select from in the location picker dialog.
 */
public class LocationCandidateAdapter
    extends RecyclerView.Adapter<LocationCandidateAdapter.ViewHolder> {

  private List<PickedLocation> candidates = Collections.emptyList();
  private final OnCandidateSelectedListener listener;

  public LocationCandidateAdapter(OnCandidateSelectedListener listener) {
    this.listener = listener;
  }

  public void setCandidates(List<PickedLocation> candidates) {
    this.candidates = candidates;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    TextView view = (TextView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_location_candidate, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    PickedLocation candidate = candidates.get(position);
    holder.text.setText(candidate.getDisplayText());
    holder.text.setOnClickListener(v -> listener.onSelected(candidate));
  }

  @Override
  public int getItemCount() {
    return candidates.size();
  }

  public interface OnCandidateSelectedListener {

    void onSelected(PickedLocation location);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    final TextView text;

    ViewHolder(TextView textView) {
      super(textView);
      this.text = textView;
    }
  }
}
