/*
* JBoss, Home of Professional Open Source.
* Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.host.controller.operations;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.ModelOnlyWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.domain.controller.LocalHostControllerInfo;
import org.jboss.as.domain.controller.operations.DomainModelReferenceValidator;
import org.jboss.as.domain.controller.operations.coordination.ServerOperationResolver;
import org.jboss.as.host.controller.resources.ServerConfigResourceDefinition;
import org.jboss.dmr.ModelNode;

/**
 * Writes the group and socket-binding-group attributes of a server group and validates the new value. ServerOperationResolver is responsible for
 * putting the affected server in the restart-required state.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public abstract class ServerRestartRequiredServerConfigWriteAttributeHandler extends ModelOnlyWriteAttributeHandler {

    public static final ModelOnlyWriteAttributeHandler SERVER_GROUP_INSTANCE = new ModelOnlyWriteAttributeHandler(ServerConfigResourceDefinition.GROUP);
    public static final ModelOnlyWriteAttributeHandler SOCKET_BINDING_GROUP_INSTANCE = new ModelOnlyWriteAttributeHandler(ServerConfigResourceDefinition.SOCKET_BINDING_GROUP);
    public static final ModelOnlyWriteAttributeHandler SOCKET_BINDING_PORT_OFFSET_INSTANCE = new ModelOnlyWriteAttributeHandler(ServerConfigResourceDefinition.SOCKET_BINDING_PORT_OFFSET);

    protected ServerRestartRequiredServerConfigWriteAttributeHandler(AttributeDefinition attributeDefinition) {
        super(attributeDefinition);
    }

    public static ModelOnlyWriteAttributeHandler createGroupInstance(LocalHostControllerInfo hostControllerInfo) {
        return SERVER_GROUP_INSTANCE;
    }

    public static ModelOnlyWriteAttributeHandler createSocketBindingGroupInstance(LocalHostControllerInfo hostControllerInfo) {
        return SOCKET_BINDING_GROUP_INSTANCE;
    }

    @Override
    protected void finishModelStage(OperationContext context, ModelNode operation, String attributeName, ModelNode newValue,
            ModelNode currentValue, Resource resource) throws OperationFailedException {
        if (newValue.equals(currentValue)) {
            //Set an attachment to avoid propagation to the servers, we don't want them to go into restart-required if nothing changed
            ServerOperationResolver.addToDontPropagateToServersAttachment(context, operation);
        }
        // Validate the model references
        context.addStep(DomainModelReferenceValidator.INSTANCE, OperationContext.Stage.MODEL);
    }

}
