/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.integration.insert;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.mule.api.processor.MessageProcessor;
import org.mule.common.Result;
import org.mule.common.metadata.DefaultListMetaDataModel;
import org.mule.common.metadata.DefaultParameterizedMapMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.SimpleMetaDataModel;
import org.mule.common.metadata.datatype.DataType;
import org.mule.construct.Flow;
import org.mule.module.db.integration.AbstractDbIntegrationTestCase;
import org.mule.module.db.integration.model.AbstractTestDatabase;
import org.mule.module.db.integration.TestDbConfig;
import org.mule.module.db.internal.processor.AbstractSingleQueryDbMessageProcessor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized;

public class InsertOutputMetadataTestCase extends AbstractDbIntegrationTestCase
{

    public InsertOutputMetadataTestCase(String dataSourceConfigResource, AbstractTestDatabase testDatabase)
    {
        super(dataSourceConfigResource, testDatabase);
    }

    @Parameterized.Parameters
    public static List<Object[]> parameters()
    {
        return TestDbConfig.getResources();
    }

    @Override
    protected String[] getFlowConfigurationResources()
    {
        return new String[] {"integration/insert/insert-output-metadata-config.xml"};
    }

    @Test
    public void returnsInsertOutputMetadata() throws Exception
    {
        Flow flowConstruct = (Flow) muleContext.getRegistry().lookupFlowConstruct("insertMetadata");

        List<MessageProcessor> messageProcessors = flowConstruct.getMessageProcessors();
        AbstractSingleQueryDbMessageProcessor queryMessageProcessor = (AbstractSingleQueryDbMessageProcessor) messageProcessors.get(0);
        Result<MetaData> outputMetaData = queryMessageProcessor.getOutputMetaData(null);

        SimpleMetaDataModel simpleMetaDataModel = (SimpleMetaDataModel) outputMetaData.get().getPayload();
        assertThat(simpleMetaDataModel.getDataType(), equalTo(DataType.DOUBLE));
    }

    @Test
    public void returnsInsertAutoGeneratedKeysOutputMetadata() throws Exception
    {
        Flow flowConstruct = (Flow) muleContext.getRegistry().lookupFlowConstruct("insertAutoGeneratedKeysMetadata");

        List<MessageProcessor> messageProcessors = flowConstruct.getMessageProcessors();
        AbstractSingleQueryDbMessageProcessor queryMessageProcessor = (AbstractSingleQueryDbMessageProcessor) messageProcessors.get(0);
        Result<MetaData> outputMetaData = queryMessageProcessor.getOutputMetaData(null);

        DefaultListMetaDataModel listMetaDataModel = (DefaultListMetaDataModel) outputMetaData.get().getPayload();
        assertEquals(ArrayList.class.getName(), listMetaDataModel.getImplementationClass());
        DefaultParameterizedMapMetaDataModel mapMetaDataModel = (DefaultParameterizedMapMetaDataModel) listMetaDataModel.getElementModel();
        assertEquals(DataType.STRING, mapMetaDataModel.getKeyMetaDataModel().getDataType());
        assertEquals(DataType.POJO, mapMetaDataModel.getValueMetaDataModel().getDataType());
    }
}
