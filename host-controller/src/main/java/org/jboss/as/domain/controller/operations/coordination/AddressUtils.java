/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

package org.jboss.as.domain.controller.operations.coordination;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.COMPOSITE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.HOST;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.STEPS;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.dmr.ModelNode;

/**
 * @author Emanuel Muckenhuber
 */
class AddressUtils {

    static ModelNode processOperation(final ModelNode operation, final String hostName) {

        final String op = operation.get(OP).asString();
        if (COMPOSITE.equals(op)) {
            final ModelNode result = new ModelNode();
            result.get(OP).set(COMPOSITE);
            result.get(OP_ADDR).setEmptyList();
            final ModelNode steps = result.get(STEPS);
            steps.setEmptyList();
            for (final ModelNode step : operation.get(STEPS).asList()) {
                steps.add(processOperation(step, hostName));
            }
            return result;
        } else {
            final PathAddress address = PathAddress.pathAddress(operation.get(OP_ADDR));
            if (address.size() > 0) {
                final PathElement first = address.getElement(0);
                if (first.isMultiTarget() && HOST.equals(first.getKey())) {
                    final ModelNode clone = operation.clone();
                    PathAddress resulting = PathAddress.pathAddress(PathElement.pathElement(HOST, hostName));
                    if (address.size() > 1) {
                        resulting = resulting.append(address.subAddress(1));
                    }
                    clone.get(OP_ADDR).set(resulting.toModelNode());
                    return clone;
                } else {
                    return operation;
                }
            } else {
                return operation;
            }
        }
    }

}
