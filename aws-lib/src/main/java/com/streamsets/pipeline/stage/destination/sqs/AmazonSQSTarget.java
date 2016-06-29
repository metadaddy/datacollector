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
package com.streamsets.pipeline.stage.destination.sqs;

import com.streamsets.pipeline.api.Batch;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseTarget;
import com.streamsets.pipeline.stage.lib.sqs.AmazonSQSErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Skeleton Destination Code for StreamSets Data Collector
 */
public class AmazonSQSTarget extends BaseTarget {
  private static final Logger LOG = LoggerFactory.getLogger(AmazonSQSTarget.class);

  private final String config;

  public AmazonSQSTarget(
      final String config
  ) {
    this.config = config;
  }

  @Override
  protected List<ConfigIssue> init() {
    LOG.info("AmazonSQSTarget.init called");

    // Validate configuration values and open any required resources.
    List<ConfigIssue> issues = super.init();

    if (config.equals("invalidValue")) {
      issues.add(
          getContext().createConfigIssue(
              Groups.SQS.name(), "config", AmazonSQSErrors.SKELETON_00, "Here's what's wrong..."
          )
      );
    }

    // If issues is not empty, the UI will inform the user of each configuration issue in the list.
    return issues;
  }

  @Override
  public void destroy() {
    // Clean up any open resources.
    LOG.info("AmazonSQSTarget.destroy called");

    super.destroy();
  }

  @Override
  @SuppressWarnings("unchecked")
  public void write(Batch batch) throws StageException {
    // TODO: write the records to your final destination
    LOG.info("AmazonSQSTarget.write called for batch {}", batch.toString());

    Iterator<Record> batchIterator = batch.getRecords();

    while (batchIterator.hasNext()) {
      Record record = batchIterator.next();

      LOG.info("AmazonSQSTarget.write iterating over record {}", record.toString());
    }
  }
}
