package io.github.cloudiator.messaging;

import de.uniulm.omi.cloudiator.sword.domain.Api;
import de.uniulm.omi.cloudiator.sword.domain.ApiBuilder;
import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import org.cloudiator.messages.entities.IaasEntities;

/**
 * Created by daniel on 31.05.17.
 */
class ApiMessageToApi implements TwoWayConverter<IaasEntities.Api, Api> {

  public static final ApiMessageToApi INSTANCE = new ApiMessageToApi();

  private ApiMessageToApi() {}

  @Override
  public Api apply(IaasEntities.Api api) {
    return ApiBuilder.newBuilder().providerName(api.getProviderName()).build();
  }

  @Override
  public IaasEntities.Api applyBack(Api api) {
    return IaasEntities.Api.newBuilder().setProviderName(api.providerName()).build();
  }
}
