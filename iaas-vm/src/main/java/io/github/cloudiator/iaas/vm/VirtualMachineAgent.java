package io.github.cloudiator.iaas.vm;

import com.google.common.base.MoreObjects;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import de.uniulm.omi.cloudiator.util.configuration.Configuration;
import de.uniulm.omi.cloudiator.util.execution.ExecutionService;
import de.uniulm.omi.cloudiator.util.execution.LoggingScheduledThreadPoolExecutor;
import de.uniulm.omi.cloudiator.util.execution.ScheduledThreadPoolExecutorExecutionService;
import io.github.cloudiator.iaas.vm.config.VmAgentModule;
import io.github.cloudiator.iaas.vm.messaging.CloudCreatedSubscriber;
import io.github.cloudiator.iaas.vm.messaging.CreateVirtualMachineSubscriber;
import io.github.cloudiator.iaas.vm.messaging.VirtualMachineDeleteRequestSubscriber;
import io.github.cloudiator.iaas.vm.messaging.VirtualMachineQuerySubscriber;
import io.github.cloudiator.iaas.vm.messaging.VirtualMachineRequestQueueWorker;
import io.github.cloudiator.persistance.JpaModule;
import io.github.cloudiator.util.JpaContext;
import java.util.concurrent.TimeUnit;
import org.cloudiator.messaging.kafka.KafkaContext;
import org.cloudiator.messaging.kafka.KafkaMessagingModule;
import org.cloudiator.messaging.services.MessageServiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachineAgent {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(VirtualMachineAgent.class);

  private static Injector injector =
      Guice.createInjector(
          new KafkaMessagingModule(new KafkaContext(Configuration.conf())),
          new MessageServiceModule(),
          new JpaModule("defaultPersistenceUnit", new JpaContext(
              Configuration.conf())),
          new VmAgentModule(new VmAgentContext()));

  private static final ExecutionService EXECUTION_SERVICE = new ScheduledThreadPoolExecutorExecutionService(
      new LoggingScheduledThreadPoolExecutor(numberOfWorkers()));


  private static int numberOfWorkers() {
    return injector.getInstance(Key.get(Integer.class,
        Names.named(Constants.VM_PARALLEL_STARTS)));
  }

  /**
   * starts the virtual machine agent.
   *
   * @param args args
   */
  public static void main(String[] args) {

    LOGGER.info(String.format("%s is starting.", VirtualMachineAgent.class.getName()));

    LOGGER.info(String.format("%s is registering shutdown hook for work execution service %s",
        VirtualMachineAgent.class.getName(), EXECUTION_SERVICE));
    EXECUTION_SERVICE.delayShutdownHook(5, TimeUnit.MINUTES);

    LOGGER.info(String
        .format("%s is starting %s virtual machine workers", VirtualMachineAgent.class.getName(),
            numberOfWorkers()));
    for (int i = 0; i < numberOfWorkers(); i++) {
      EXECUTION_SERVICE.execute(injector.getInstance(VirtualMachineRequestQueueWorker.class));
    }

    LOGGER.info(String.format("%s is starting %s.", VirtualMachineAgent.class.getName(),
        CloudCreatedSubscriber.class.getName()));
    injector.getInstance(CloudCreatedSubscriber.class).run();
    LOGGER.info(String.format("%s is starting %s.", VirtualMachineAgent.class.getName(),
        CreateVirtualMachineSubscriber.class.getName()));
    injector.getInstance(CreateVirtualMachineSubscriber.class).run();
    LOGGER.info(String.format("%s is starting %s.", VirtualMachineAgent.class.getName(),
        VirtualMachineQuerySubscriber.class.getName()));
    injector.getInstance(VirtualMachineQuerySubscriber.class).run();
    LOGGER.info(String.format("%s is starting %s.", VirtualMachineAgent.class.getName(),
        VirtualMachineDeleteRequestSubscriber.class.getName()));
    injector.getInstance(VirtualMachineDeleteRequestSubscriber.class).run();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        //createSubscriber.terminate();
      }
    });
    LOGGER.info(String.format("%s started.", VirtualMachineAgent.class.getName()));
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).toString();
  }
}
