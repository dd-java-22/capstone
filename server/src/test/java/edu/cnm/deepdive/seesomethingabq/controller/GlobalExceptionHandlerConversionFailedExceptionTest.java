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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerConversionFailedExceptionTest {

  @Test
  void conversionFailedIncludesParameterNameWhenUniquelyMatchable() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("pageNumber", "not-an-int");
    ConversionFailedException ex = new ConversionFailedException(
        TypeDescriptor.valueOf(String.class),
        TypeDescriptor.valueOf(Integer.class),
        "not-an-int",
        new NumberFormatException("For input string: \"not-an-int\"")
    );

    GlobalExceptionHandler.ErrorResponse response = handler.handleConversionFailed(ex, request);

    assertTrue(response.message().contains("pageNumber"));
    assertTrue(response.message().contains("parameter"));
  }

  @Test
  void conversionFailedFallsBackWhenNameIsAmbiguous() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("a", "same");
    request.addParameter("b", "same");
    ConversionFailedException ex = new ConversionFailedException(
        TypeDescriptor.valueOf(String.class),
        TypeDescriptor.valueOf(Integer.class),
        "same",
        new NumberFormatException("For input string: \"same\"")
    );

    GlobalExceptionHandler.ErrorResponse response = handler.handleConversionFailed(ex, request);

    assertTrue(response.message().contains("Invalid value"));
    assertTrue(!response.message().contains("parameter 'a'"));
    assertTrue(!response.message().contains("parameter 'b'"));
  }

}

