package org.cloudiator.iaas.node;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.sword.domain.VirtualMachine;
import io.github.cloudiator.domain.Node;
import io.github.cloudiator.domain.NodeCandidate;
import io.github.cloudiator.messaging.VirtualMachineMessageToVirtualMachine;
import java.util.concurrent.ExecutionException;
import org.cloudiator.messages.Vm.CreateVirtualMachineRequestMessage;
import org.cloudiator.messages.Vm.VirtualMachineCreatedResponse;
import org.cloudiator.messages.entities.IaasEntities.VirtualMachineRequest;
import org.cloudiator.messaging.SettableFutureResponseCallback;
import org.cloudiator.messaging.services.VirtualMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachineNodeIncarnation implements NodeCandidateIncarnation {

  public static class VirtualMachineNodeIncarnationFactory implements
      NodeCandidateIncarnationFactory {

    private final VirtualMachineService virtualMachineService;

    @Inject
    public VirtualMachineNodeIncarnationFactory(
        VirtualMachineService virtualMachineService) {
      this.virtualMachineService = virtualMachineService;
    }

    @Override
    public boolean canIncarnate(NodeCandidate nodeCandidate) {
      //we currently only have virtual machines, so we can always incarnate
      //todo: adopt logic when this is no longer the case
      return true;
    }

    @Override
    public NodeCandidateIncarnation create(String groupName, String userId) {
      return new VirtualMachineNodeIncarnation(groupName, userId, virtualMachineService);
    }
  }

  private static final Logger LOGGER = LoggerFactory
      .getLogger(VirtualMachineNodeIncarnation.class);
  private final String groupName;
  private final String userId;
  private final VirtualMachineService virtualMachineService;
  private static final NameGenerator NAME_GENERATOR = NameGenerator.INSTANCE;
  private static final VirtualMachineMessageToVirtualMachine VIRTUAL_MACHINE_CONVERTER = VirtualMachineMessageToVirtualMachine.INSTANCE;
  private static final VirtualMachineToNode VIRTUAL_MACHINE_TO_NODE = VirtualMachineToNode.INSTANCE;


  public VirtualMachineNodeIncarnation(String groupName,
      String userId, VirtualMachineService virtualMachineService) {
    this.groupName = groupName;
    this.userId = userId;
    this.virtualMachineService = virtualMachineService;
  }

  @Override
  public Node apply(NodeCandidate nodeCandidate) throws ExecutionException {

    final SettableFutureResponseCallback<VirtualMachineCreatedResponse, VirtualMachine> virtualMachineFuture = SettableFutureResponseCallback
        .create(
            virtualMachineCreatedResponse -> VIRTUAL_MACHINE_CONVERTER
                .apply(virtualMachineCreatedResponse.getVirtualMachine()));

    final VirtualMachineRequest virtualMachineRequest = generateRequest(nodeCandidate);
    CreateVirtualMachineRequestMessage createVirtualMachineRequestMessage = CreateVirtualMachineRequestMessage
        .newBuilder().setUserId(userId).setVirtualMachineRequest(virtualMachineRequest)
        .build();

    LOGGER.debug(String
        .format("%s is sending virtual machine request %s for node candidate %s.", this,
            virtualMachineRequest, nodeCandidate));

    virtualMachineService
        .createVirtualMachineAsync(createVirtualMachineRequestMessage, virtualMachineFuture);

    try {
      final VirtualMachine virtualMachine = virtualMachineFuture.get();

      LOGGER.debug(String
          .format("%s incarnated nodeCandidate %s as virtual machine %s.", this, nodeCandidate,
              virtualMachine));

      return VIRTUAL_MACHINE_TO_NODE.apply(virtualMachine);

    } catch (InterruptedException e) {
      throw new IllegalStateException("Got interrupted while waiting for virtual machine to start",
          e);
    }
  }

  private VirtualMachineRequest generateRequest(NodeCandidate nodeCandidate) {

    return VirtualMachineRequest.newBuilder()
        .setHardware(nodeCandidate.hardware().id())
        .setImage(nodeCandidate.image().id())
        .setLocation(nodeCandidate.location().id())
        .setName(NAME_GENERATOR.generate(this.groupName))
        .build();
  }

}