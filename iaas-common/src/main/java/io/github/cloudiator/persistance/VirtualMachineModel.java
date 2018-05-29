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


import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Created by daniel on 31.10.14.
 */
@Entity
class VirtualMachineModel extends ResourceModel {

  @OneToOne(cascade = CascadeType.ALL)
  @Nullable
  private LoginCredentialModel loginCredential;

  @Nullable
  @ManyToOne()
  private ImageModel imageModel;
  @Nullable
  @ManyToOne()
  private HardwareModel hardwareModel;

  @OneToOne
  @Nullable
  private IpGroupModel ipGroup;

  /**
   * Empty constructor for hibernate.
   */
  protected VirtualMachineModel() {
  }

  public VirtualMachineModel(String cloudUniqueId, String providerId, String name,
      CloudModel cloudModel,
      @Nullable LocationModel locationModel,
      @Nullable LoginCredentialModel loginCredential,
      @Nullable ImageModel imageModel,
      @Nullable HardwareModel hardwareModel, @Nullable IpGroupModel ipGroup) {
    super(cloudUniqueId, providerId, name, cloudModel, locationModel);
    this.loginCredential = loginCredential;
    this.imageModel = imageModel;
    this.hardwareModel = hardwareModel;
    this.ipGroup = ipGroup;
  }


  @Nullable
  public LoginCredentialModel getLoginCredential() {
    return loginCredential;
  }

  @Nullable
  public ImageModel getImageModel() {
    return imageModel;
  }

  @Nullable
  public HardwareModel getHardwareModel() {
    return hardwareModel;
  }

  @Nullable
  public IpGroupModel getIpGroup() {
    return ipGroup;
  }
}
