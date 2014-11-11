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

package org.jboss.as.test.integration.domain.suites;

import java.io.IOException;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.test.integration.domain.management.util.DomainLifecycleUtil;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.as.test.integration.domain.management.util.DomainTestUtils;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.dmr.Property;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Emanuel Muckenhuber
 */
public class WildcardOperationsTestCase {

    private static final String WILDCARD = "*";

    private static DomainTestSupport testSupport;
    private static DomainLifecycleUtil domainMasterLifecycleUtil;
    private static DomainLifecycleUtil domainSlaveLifecycleUtil;

    @BeforeClass
    public static void setupDomain() throws Exception {
        testSupport = DomainTestSuite.createSupport(WildcardOperationsTestCase.class.getSimpleName());
        domainMasterLifecycleUtil = testSupport.getDomainMasterLifecycleUtil();
        domainSlaveLifecycleUtil = testSupport.getDomainSlaveLifecycleUtil();
    }

    @AfterClass
    public static void tearDownDomain() throws Exception {
        testSupport = null;
        domainMasterLifecycleUtil = null;
        domainSlaveLifecycleUtil = null;
        DomainTestSuite.stopSupport();
    }

    @Test
    public void testBasicWildcardOperations() throws IOException, MgmtOperationException {

        final ModelNode address = new ModelNode();
        address.setEmptyList();

        // host=*
        address.add(ModelDescriptionConstants.HOST, WILDCARD);
        executeReadResource(address, domainMasterLifecycleUtil.getDomainClient());

        // host=*,server=*
        address.add(ModelDescriptionConstants.RUNNING_SERVER, WILDCARD);
        executeReadResource(address, domainMasterLifecycleUtil.getDomainClient());

        // host=*,server=*,subsystem=*
        address.add(ModelDescriptionConstants.SUBSYSTEM, WILDCARD);
        executeReadResource(address, domainMasterLifecycleUtil.getDomainClient());

        address.setEmptyList();

    }

    @Test
    public void testNormalCompositeOperation() throws IOException, MgmtOperationException {

        final ModelNode composite = new ModelNode();
        composite.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.COMPOSITE);
        final ModelNode steps = composite.get(ModelDescriptionConstants.STEPS);

        final ModelNode address = new ModelNode();
        address.setEmptyList();

        // host=*
        address.add(ModelDescriptionConstants.HOST, "slave");
        steps.add().set(createReadResourceOperation(address));

        // host=*,server=*
        address.add(ModelDescriptionConstants.RUNNING_SERVER, "main-three");
        steps.add().set(createReadResourceOperation(address));

        // host=*,server=*,subsystem=*
        address.add(ModelDescriptionConstants.SUBSYSTEM, "io");
        steps.add().set(createReadResourceOperation(address));

        final ModelNode result = DomainTestUtils.executeForResult(composite, domainMasterLifecycleUtil.getDomainClient());
        System.out.println(result);
//
//        for (Property property : result.asPropertyList()) {
//            Assert.assertEquals(ModelType.LIST, property.getValue().get(ModelDescriptionConstants.RESULT).getType());
//        }

        address.setEmptyList();
        steps.setEmptyList();

    }

    @Test
    public void testCompositeOperation() throws IOException, MgmtOperationException {

        final ModelNode composite = new ModelNode();
        composite.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.COMPOSITE);
        final ModelNode steps = composite.get(ModelDescriptionConstants.STEPS);

        final ModelNode address = new ModelNode();
        address.setEmptyList();

        // host=*
        address.add(ModelDescriptionConstants.HOST, WILDCARD);
        steps.add().set(createReadResourceOperation(address));

        // host=*,server=*
        address.add(ModelDescriptionConstants.RUNNING_SERVER, WILDCARD);
        steps.add().set(createReadResourceOperation(address));

        // host=*,server=*,subsystem=*
        address.add(ModelDescriptionConstants.SUBSYSTEM, WILDCARD);
        steps.add().set(createReadResourceOperation(address));

        final ModelNode result = DomainTestUtils.executeForResult(composite, domainMasterLifecycleUtil.getDomainClient());

        for (Property property : result.asPropertyList()) {
            Assert.assertEquals(ModelType.LIST, property.getValue().get(ModelDescriptionConstants.RESULT).getType());
        }

        address.setEmptyList();
        steps.setEmptyList();

    }

    static ModelNode executeReadResource(final ModelNode address, final ModelControllerClient client) throws IOException, MgmtOperationException {
        final ModelNode operation = createReadResourceOperation(address);
        return assertWildcardResult(operation, client);
    }

    static ModelNode createReadResourceOperation(final ModelNode address) {
        final ModelNode operation = new ModelNode();
        operation.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_RESOURCE_OPERATION);
        operation.get(ModelDescriptionConstants.OP_ADDR).set(address);
        return operation;
    }

    static ModelNode assertWildcardResult(final ModelNode operation, final ModelControllerClient client) throws IOException, MgmtOperationException {
        final ModelNode result = DomainTestUtils.executeForResult(operation, client);

        Assert.assertEquals(ModelType.LIST, result.getType());

        for (final ModelNode r : result.asList()) {
            Assert.assertTrue(r.hasDefined(ModelDescriptionConstants.OP_ADDR));
            Assert.assertTrue(r.hasDefined(ModelDescriptionConstants.RESULT));
        }
        return result;
    }

}
