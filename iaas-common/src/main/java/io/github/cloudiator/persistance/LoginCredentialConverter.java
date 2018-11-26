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

package io.github.cloudiator.persistance;

import de.uniulm.omi.cloudiator.sword.domain.LoginCredential;
import de.uniulm.omi.cloudiator.sword.domain.LoginCredentialBuilder;
import de.uniulm.omi.cloudiator.util.OneWayConverter;
import javax.annotation.Nullable;

class LoginCredentialConverter implements
    OneWayConverter<LoginCredentialModel, LoginCredential> {

  @Nullable
  @Override
  public LoginCredential apply(@Nullable LoginCredentialModel loginCredentialModel) {
    if (loginCredentialModel == null) {
      return null;
    }

    return LoginCredentialBuilder.newBuilder().password(loginCredentialModel.getPassword())
        .privateKey(loginCredentialModel.getPrivateKey())
        .username(loginCredentialModel.getUsername()).build();
  }
}
