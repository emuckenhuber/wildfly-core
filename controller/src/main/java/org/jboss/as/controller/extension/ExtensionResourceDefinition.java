/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.controller.extension;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.EXTENSION;

import java.util.List;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.ResourceFactoryDescription;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.access.management.AccessConstraintDefinition;
import org.jboss.as.controller.access.management.SensitiveTargetAccessConstraintDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.descriptions.common.ControllerResolver;
import org.jboss.as.controller.operations.validation.StringLengthValidator;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import org.jboss.as.controller.registry.Resource;
import org.jboss.dmr.ModelType;

/**
 * {@link SimpleResourceDefinition} for an {@link org.jboss.as.controller.Extension} resource.
 *
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public class ExtensionResourceDefinition extends SimpleResourceDefinition implements ResourceFactoryDescription {

    public static final SimpleAttributeDefinition MODULE = new SimpleAttributeDefinitionBuilder(ModelDescriptionConstants.MODULE, ModelType.STRING, false)
            .setValidator(new StringLengthValidator(1)).build();

    private final List<AccessConstraintDefinition> accessConstraints;

    final ExtensionRegistry extensionRegistry;

    public ExtensionResourceDefinition(final ExtensionRegistry extensionRegistry, final boolean parallelBoot, final boolean standalone, final boolean slaveHC) {
        super(PathElement.pathElement(EXTENSION), ControllerResolver.getResolver(EXTENSION),
                new ExtensionAddHandler(extensionRegistry, parallelBoot, standalone, slaveHC), new ExtensionRemoveHandler(extensionRegistry),
                OperationEntry.Flag.RESTART_NONE, OperationEntry.Flag.RESTART_NONE);
        this.accessConstraints = SensitiveTargetAccessConstraintDefinition.EXTENSIONS.wrapAsList();
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    public boolean registerByDefault() {
        return false;
    }

    @Override
    public Resource createResource(PathElement pathElement) throws OperationFailedException {
        final String moduleName = pathElement.getValue();
        return new ExtensionResource(moduleName, extensionRegistry);
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerReadOnlyAttribute(MODULE, null);
    }

    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        resourceRegistration.registerSubModel(new ExtensionSubsystemResourceDefinition());
    }

    @Override
    public List<AccessConstraintDefinition> getAccessConstraints() {
        return accessConstraints;
    }
}
