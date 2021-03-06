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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import io.github.cloudiator.domain.ExtendedCloud;
import io.github.cloudiator.util.CollectorsUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.cloudiator.messages.Cloud.CloudQueryRequest;
import org.cloudiator.messaging.ResponseException;
import org.cloudiator.messaging.services.CloudService;

public class CloudMessageRepository implements MessageRepository<ExtendedCloud> {

  private final static String RESPONSE_ERROR = "Could not retrieve cloud object(s) due to error %s";
  private static final CloudMessageToCloudConverter CONVERTER = CloudMessageToCloudConverter.INSTANCE;
  private final CloudService cloudService;

  @Inject
  public CloudMessageRepository(CloudService cloudService) {
    checkNotNull(cloudService, "cloudService is null");
    this.cloudService = cloudService;
  }

  @Override
  public ExtendedCloud getById(String userId, String id) {
    try {
      return cloudService
          .getClouds(CloudQueryRequest.newBuilder().setUserId(userId).setCloudId(id).build())
          .getCloudsList().stream()
          .map(CONVERTER)
          .collect(CollectorsUtil.singletonCollector());
    } catch (ResponseException e) {
      throw new IllegalStateException(String.format(RESPONSE_ERROR, e.getMessage()), e);
    }
  }

  @Override
  public List<ExtendedCloud> getAll(String userId) {
    try {
      return cloudService.getClouds(CloudQueryRequest.newBuilder().setUserId(userId).build())
          .getCloudsList().stream().map(CONVERTER).collect(
              Collectors.toList());
    } catch (ResponseException e) {
      throw new IllegalStateException(String.format(RESPONSE_ERROR, e.getMessage()), e);
    }
  }
}
