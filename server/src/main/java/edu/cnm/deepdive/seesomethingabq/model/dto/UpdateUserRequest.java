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
package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Data transfer object for updating user profile information.
 * Both fields are nullable - only provided fields will be updated.
 */
public class UpdateUserRequest {

  private String displayName;
  private String email;

  /**
   * Returns the display name.
   *
   * @return Display name, or null if not being updated.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Sets the display name.
   *
   * @param displayName Display name.
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Returns the email address.
   *
   * @return Email address, or null if not being updated.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address.
   *
   * @param email Email address.
   */
  public void setEmail(String email) {
    this.email = email;
  }
}
