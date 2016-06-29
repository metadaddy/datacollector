/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.origin.sqs;

import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseSource;
import com.streamsets.pipeline.stage.lib.sqs.AmazonSQSErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmazonSQSSource extends BaseSource {
  private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSSource.class);

  private final String config;

  public AmazonSQSSource(String config) {
    this.config = config;
  }

  @Override
  protected List<ConfigIssue> init() {
    // Validate configuration values and open any required resources.
    List<ConfigIssue> issues = super.init();

    if (config.equals("invalidValue")) {
      issues.add(
          getContext().createConfigIssue(
              Groups.SQS.name(), "config", AmazonSQSErrors.SKELETON_01, "Here's what's wrong..."
          )
      );
    }

    // If issues is not empty, the UI will inform the user of each configuration issue in the list.
    return issues;
  }

  @Override
  public void destroy() {
    // Clean up any open resources.
    super.destroy();
  }

  @Override
  public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
    // Offsets can vary depending on the data source. Here we use an integer as an example only.
    long nextSourceOffset = 0;
    if (lastSourceOffset != null) {
      nextSourceOffset = Long.parseLong(lastSourceOffset);
    }

    int numRecords = 0;

    // TODO: As the developer, implement your logic that reads from a data source in this method.

    // Create records and add to batch. Records must have a string id. This can include the source offset
    // or other metadata to help uniquely identify the record itself.
    while (numRecords < maxBatchSize) {
      Record record = getContext().createRecord("some-id::" + nextSourceOffset);
      Map<String, Field> map = new HashMap<>();
      map.put("fieldName", Field.create("Some Value"));
      record.set(Field.create(map));
      batchMaker.addRecord(record);
      ++nextSourceOffset;
      ++numRecords;
    }

    return String.valueOf(nextSourceOffset);
  }
}
