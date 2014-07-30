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

package org.jboss.as.controller;

import org.jboss.as.controller.registry.Resource;
import org.jboss.as.controller.registry.ResourceFactory;

/**
 * @author Emanuel Muckenhuber
 */
public interface ResourceFactoryDescription extends ResourceFactory {

    /**
     * Whether this resource should automatically get registered when the parent gets created. This basically does not
     * require
     *
     * @return {@code true} if the resource should be registered by default, {@code false} otherwise
     */
    boolean registerByDefault();

    ResourceFactoryDescription DEFAULT = new ResourceFactoryDescription() {
        @Override
        public boolean registerByDefault() {
            return false;
        }

        @Override
        public Resource createResource(PathElement pathElement) throws OperationFailedException {
            return ResourceFactory.DEFAULT.createResource(pathElement);
        }
    };

}
