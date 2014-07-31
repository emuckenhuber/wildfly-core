package org.jboss.as.platform.mbean;

import java.lang.management.ManagementFactory;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ResourceFactoryDescription;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;

/**
 * @author Tomaz Cerar (c) 2013 Red Hat Inc.
 */
public class PlatformMBeanResourceDefinition extends SimpleResourceDefinition implements ResourceFactoryDescription {
    static final PlatformMBeanResourceDefinition INSTANCE = new PlatformMBeanResourceDefinition();

    private PlatformMBeanResourceDefinition() {
        super(PlatformMBeanConstants.PLATFORM_MBEAN_PATH,
                PlatformMBeanUtil.getResolver("platform-mbeans"));
    }

    @Override
    public boolean registerByDefault() {
        return true;
    }

    @Override
    public Resource createResource(PathElement pathElement) throws OperationFailedException {
        return new RootPlatformMBeanResource();
    }

    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        super.registerChildren(resourceRegistration);
        resourceRegistration.registerSubModel(ClassLoadingResourceDefinition.INSTANCE);
        if (ManagementFactory.getCompilationMXBean() != null) {
            resourceRegistration.registerSubModel(CompilationResourceDefinition.INSTANCE);
        }
        resourceRegistration.registerSubModel(GarbageCollectorRootResourceDefinition.INSTANCE);
        resourceRegistration.registerSubModel(MemoryManagerRootResourceDefinition.INSTANCE);
        resourceRegistration.registerSubModel(MemoryResourceDefinition.INSTANCE);
        resourceRegistration.registerSubModel(MemoryPoolRootResourceDefinition.INSTANCE);
        resourceRegistration.registerSubModel(OperatingSystemResourceDefinition.INSTANCE);
        resourceRegistration.registerSubModel(RuntimeResourceDefinition.INSTANCE);
        resourceRegistration.registerSubModel(ThreadResourceDefinition.INSTANCE);

        resourceRegistration.registerSubModel(BufferPoolRootResourceDefinition.INSTANCE);
    }
}
