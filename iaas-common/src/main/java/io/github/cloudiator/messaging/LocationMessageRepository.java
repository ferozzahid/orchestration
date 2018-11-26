/*
 * Copyright (c) 2014-2018 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.github.cloudiator.messaging;

import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.sword.domain.Location;
import java.util.List;
import java.util.stream.Collectors;
import org.cloudiator.messages.Location.LocationQueryRequest;
import org.cloudiator.messaging.ResponseException;
import org.cloudiator.messaging.services.LocationService;

public class LocationMessageRepository implements MessageRepository<Location> {

  private final static String RESPONSE_ERROR = "Could not retrieve hardware flavor object(s) due to error %s";
  private static final LocationMessageToLocationConverter CONVERTER = LocationMessageToLocationConverter.INSTANCE;
  private final LocationService locationService;

  @Inject
  public LocationMessageRepository(
      LocationService locationService) {
    this.locationService = locationService;
  }

  @Override
  public Location getById(String userId, String id) {
    try {
      final List<Location> collect = locationService
          .getLocations(
              LocationQueryRequest.newBuilder().setLocationId(id).setUserId(userId).build())
          .getLocationsList().stream().map(CONVERTER).collect(Collectors.toList());

      checkState(collect.size() <= 1, "Expected unique result.");

      if (collect.isEmpty()) {
        return null;
      }
      return collect.get(0);


    } catch (ResponseException e) {
      throw new IllegalStateException(String.format(RESPONSE_ERROR, e.getMessage()), e);
    }
  }

  @Override
  public List<Location> getAll(String userId) {
    try {
      return locationService
          .getLocations(LocationQueryRequest.newBuilder().setUserId(userId).build())
          .getLocationsList().stream().map(CONVERTER).collect(
              Collectors.toList());
    } catch (ResponseException e) {
      throw new IllegalStateException(String.format(RESPONSE_ERROR, e.getMessage()), e);
    }
  }
}
