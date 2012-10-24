/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

package org.jboss.as.patching.runner;

import org.jboss.as.patching.PatchMessages;
import org.jboss.as.patching.metadata.ContentModification;
import org.jboss.as.patching.metadata.ModificationType;
import org.jboss.as.patching.metadata.ModuleItem;
import org.jboss.marshalling.ByteInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Removing a module will create a module.xml containing a <module-absent /> element, which
 * will trigger a {@linkplain org.jboss.modules.ModuleNotFoundException} when the
 * {@linkplan org.jboss.modules.ModuleLoader} tries to load the module.
 *
 * @author Emanuel Muckenhuber
 */
class ModuleRemoveTask extends AbstractModuleTask {

    ModuleRemoveTask(PatchingTaskDescription description) {
        super(description);
    }

    @Override
    byte[] apply(PatchingContext context, PatchContentLoader loader) throws IOException {
        final File targetDir = context.getModulePatchDirectory(contentItem);
        if(! targetDir.mkdirs()) {
            throw PatchMessages.MESSAGES.cannotCreateDirectory(targetDir.getAbsolutePath());
        }
        final File moduleXml = new File(targetDir, MODULE_XML);
        final OutputStream os = new FileOutputStream(moduleXml);
        try {
            final ByteArrayInputStream is = new ByteArrayInputStream(getFileContent(contentItem));
            return PatchUtils.copyAndGetHash(is, os);
        } finally {
            PatchUtils.safeClose(os);
        }
    }

    @Override
    ContentModification createRollbackEntry(ContentModification original, byte[] targetHash, byte[] itemHash) {
        final ModuleItem item = createContentItem(contentItem, itemHash);
        return new ContentModification(item, targetHash, ModificationType.ADD);
    }

    static byte[] getFileContent(final ModuleItem item) {
        final StringBuilder builder = new StringBuilder(128);
        builder.append("<?xml version='1.0' encoding='UTF-8'?>\n<module-absent xmlns=\"urn:jboss:module:1.2\"");
        builder.append(" name=\"").append(item.getName()).append("\"");
        builder.append(" slot=\"").append(item.getSlot()).append("\"");
        builder.append(" />\n");
        return builder.toString().getBytes(Charset.forName("UTF-8"));
    }

}