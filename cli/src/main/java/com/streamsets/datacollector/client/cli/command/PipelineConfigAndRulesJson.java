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
package com.streamsets.datacollector.client.cli.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.streamsets.datacollector.client.model.PipelineConfigurationJson;
import com.streamsets.datacollector.client.model.RuleDefinitionsJson;

public class PipelineConfigAndRulesJson {
  private PipelineConfigurationJson pipelineConfig;
  private RuleDefinitionsJson pipelineRules;

  public PipelineConfigAndRulesJson(
    @JsonProperty("pipelineConfig") PipelineConfigurationJson pipelineConfig,
    @JsonProperty("pipelineRules") RuleDefinitionsJson pipelineRules) {
    this.pipelineConfig = pipelineConfig;
    this.pipelineRules = pipelineRules;
  }

  public PipelineConfigurationJson getPipelineConfig() {
    return pipelineConfig;
  }

  public void setPipelineConfig(PipelineConfigurationJson pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
  }

  public RuleDefinitionsJson getPipelineRules() {
    return pipelineRules;
  }

  public void setPipelineRules(RuleDefinitionsJson pipelineRules) {
    this.pipelineRules = pipelineRules;
  }
}
