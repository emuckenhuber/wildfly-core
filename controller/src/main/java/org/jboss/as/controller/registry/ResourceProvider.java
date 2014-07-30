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

package org.jboss.as.controller.registry;

import java.util.Set;

/**
 * A resource provider.
 *
 * NOTE: {@link #clone()} usually returns a copy of the resource provider for write operations. If {@link #clone()}
 * returns the same instance the implementation needs to take care of thread-safety.
 *
 * @author Emanuel Muckenhuber
 */
public interface ResourceProvider extends Cloneable {

    /**
     * Returns {@code true} if this resource provider contains a resource
     * for the specified name exists.
     *
     * @param name    the resource name
     * @return {@code true} if the resource exists
     */
    boolean has(String name);

    /**
     * Get a resource.
     *
     * @param name    the resource name
     * @return the resource
     */
    Resource get(String name);

    /**
     * Returns {@code true} if this provider contains resources.
     *
     * @return {@code true} if this provider contains resources
     */
    boolean hasChildren();

    /**
     * Returns a set of resource names.
     *
     * @return the set of resource names
     */
    Set<String> children();

    /**
     * Register a child resource.
     *
     * @param name        the resource name
     * @param resource    the resource
     */
    void register(String name, Resource resource);

    /**
     * Remove a specifc resource.
     *
     * @param name the resource name
     * @return the removed resource, {@code null} otherwise
     */
    Resource remove(String name);

    /**
     * Clone usually creates a copy of the resource provider.
     *
     * @return the resource provider
     */
    ResourceProvider clone();

    public abstract static class ResourceProviderRegistry {

        protected abstract void registerResourceProvider(final String type, final ResourceProvider provider);

    }

    public static class Tool {

        public static void addResourceProvider(final String name, final ResourceProvider provider, final Resource resource) {
            if (resource instanceof ResourceProviderRegistry) {
                ((ResourceProviderRegistry)resource).registerResourceProvider(name, provider);
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

}
