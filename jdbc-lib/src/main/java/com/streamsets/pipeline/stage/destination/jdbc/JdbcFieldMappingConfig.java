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
package com.streamsets.pipeline.stage.destination.jdbc;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.FieldSelectorModel;

public class JdbcFieldMappingConfig {

  /**
   * Constructor used for unit testing purposes
   * @param field
   * @param columnName
   */
  public JdbcFieldMappingConfig(final String field, final String columnName) {
    this.field = field;
    this.columnName = columnName;
    this.paramValue = "?";
  }

  /**
   * Constructor used for unit testing purposes
   * @param field
   * @param columnName
   * @param paramValue
   */
  public JdbcFieldMappingConfig(final String field, final String columnName, final String paramValue) {
    this.field = field;
    this.columnName = columnName;
    this.paramValue = paramValue;
  }

  /**
   * Parameter-less constructor required.
   */
  public JdbcFieldMappingConfig() {}

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      defaultValue = "",
      label = "SDC Field",
      description = "The field in the incoming record to output.",
      displayPosition = 10
  )
  @FieldSelectorModel(singleValued = true)
  public String field;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      defaultValue="",
      label = "Column Name",
      description = "The column name to write this field to.",
      displayPosition = 20
  )
  public String columnName;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      defaultValue = "?",
      label = "Parameterized Value",
      description = "A parameterized value to use in the JDBC insert statement. Must include a ?.",
      displayPosition = 30
      // TODO: Have this depend on a checkbox in the JdbcDTarget. Blocked by SDC-1704
  )
  public String paramValue;
}
