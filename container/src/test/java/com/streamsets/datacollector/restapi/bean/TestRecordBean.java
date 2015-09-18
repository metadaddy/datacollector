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
package com.streamsets.datacollector.restapi.bean;

import com.streamsets.datacollector.json.ObjectMapperFactory;
import com.streamsets.datacollector.record.RecordImpl;
import com.streamsets.datacollector.restapi.bean.BeanHelper;
import com.streamsets.datacollector.restapi.bean.RecordJson;
import com.streamsets.pipeline.api.Record;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestRecordBean {

  @Test
  public void testWrapRecord() throws IOException {
    List<Record> records = new ArrayList<>();
    RecordImpl r = new RecordImpl("stage", "source", null, null);
    com.streamsets.pipeline.api.Field f = com.streamsets.pipeline.api.Field.create(true);
    r.set(f);
    records.add(r);

    List<RecordJson> recordJsonList = BeanHelper.wrapRecords(records);
    System.out.println(ObjectMapperFactory.get().writeValueAsString(recordJsonList));
  }

  @Test
  public void testUnwrapRecord() throws IOException {
    String recordString = "{\n" +
      "  \"header\": {\n" +
      "    \"stageCreator\": \"com_streamsets_pipeline_lib_stage_devtest_RandomDataGenerator1423946816761\",\n" +
      "    \"sourceId\": \"random:2\",\n" +
      "    \"stagesPath\": \"com_streamsets_pipeline_lib_stage_devtest_RandomDataGenerator1423946816761\",\n" +
      "    \"trackingId\": \"random:2::com_streamsets_pipeline_lib_stage_devtest_RandomDataGenerator1423946816761\",\n" +
      "    \"previousTrackingId\": null,\n" +
      "    \"raw\": null,\n" +
      "    \"rawMimeType\": null,\n" +
      "    \"errorDataCollectorId\": null,\n" +
      "    \"errorPipelineName\": null,\n" +
      "    \"errorStage\": null,\n" +
      "    \"errorCode\": null,\n" +
      "    \"errorMessage\": null,\n" +
      "    \"errorTimestamp\": 0,\n" +
      "    \"values\": {}\n" +
      "  },\n" +
      "  \"value\": {\n" +
      "    \"path\": \"\",\n" +
      "    \"type\": \"MAP\",\n" +
      "    \"value\": {\n" +
      "      \"X\": {\n" +
      "        \"path\": \"/X\",\n" +
      "        \"type\": \"BYTE_ARRAY\",\n" +
      "        \"value\": \"U3RyZWFtU2V0cyBJbmMsIFNhbiBGcmFuY2lzY28=\"\n" +
      "      }\n" +
      "    }\n" +
      "  }\n" +
      "}";
    RecordJson recordJson =
      ObjectMapperFactory.get().readValue(recordString, RecordJson.class);

  }


}
