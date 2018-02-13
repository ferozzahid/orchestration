/*
 * Copyright (c) 2014-2017 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.github.cloudiator.persistance;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by daniel on 21.06.15.
 */
public interface ResourceRepository<T extends ResourceModel>
    extends ModelRepository<T> {

  @Nullable
  T findByCloudUniqueId(String cloudUniqueId);

  List<T> findByTenant(String tenant);

  T findByCloudUniqueIdAndTenant(String tenant, String cloudUniqueId);

  List<T> findByTenantAndCloud(String tenant, String cloudId);
}