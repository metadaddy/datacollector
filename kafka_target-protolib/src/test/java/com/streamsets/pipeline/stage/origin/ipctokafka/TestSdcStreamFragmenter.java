/**
 * Copyright 2016 StreamSets Inc.
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
package com.streamsets.pipeline.stage.origin.ipctokafka;

//Using commons-io ByteArrayOutputStream on purpose

import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.OnRecordError;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.Source;
import com.streamsets.pipeline.api.ext.ContextExtensions;
import com.streamsets.pipeline.api.ext.RecordReader;
import com.streamsets.pipeline.api.ext.RecordWriter;
import com.streamsets.pipeline.sdk.ContextInfoCreator;
import com.streamsets.pipeline.sdk.RecordCreator;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSdcStreamFragmenter {

  @Test
  public void testCopy() throws IOException {
    // empty IS
    InputStream is = new ByteArrayInputStream(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Assert.assertTrue(SdcStreamFragmenter.copy(is, baos, 1));
    Assert.assertEquals(0, baos.size());

    // IS size + 1 == limit
    is = new ByteArrayInputStream(new byte[10]);
    baos = new ByteArrayOutputStream();
    Assert.assertTrue(SdcStreamFragmenter.copy(is, baos, 11));
    Assert.assertEquals(10, baos.size());

    // IS size == limit
    is = new ByteArrayInputStream(new byte[10]);
    baos = new ByteArrayOutputStream();
    Assert.assertFalse(SdcStreamFragmenter.copy(is, baos, 10));
    Assert.assertEquals(10, baos.size());
    Assert.assertTrue(SdcStreamFragmenter.copy(is, baos, 1));
    Assert.assertEquals(10, baos.size());

    // IS size > limit
    is = new ByteArrayInputStream(new byte[15]);
    baos = new ByteArrayOutputStream();
    Assert.assertFalse(SdcStreamFragmenter.copy(is, baos, 10));
    Assert.assertEquals(10, baos.size());
    Assert.assertTrue(SdcStreamFragmenter.copy(is, baos, 10));
    Assert.assertEquals(15, baos.size());

    // verify copy fidelity
    byte[] arr = new byte[15];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = (byte) i;
    }
    is = new ByteArrayInputStream(arr);
    baos = new ByteArrayOutputStream();
    Assert.assertFalse(SdcStreamFragmenter.copy(is, baos, 10));
    Assert.assertEquals(10, baos.size());
    Assert.assertTrue(SdcStreamFragmenter.copy(is, baos, 10));
    Assert.assertEquals(15, baos.size());
    Assert.assertArrayEquals(arr, baos.toByteArray());
  }

  @Test
  public void testFindEndOfLastLineBeforeLimit() throws IOException {
    // empty array
    byte[] arr = new byte[0];
    Assert.assertEquals(-1, SdcStreamFragmenter.findEndOfLastLineBeforeLimit(arr, 0));

    // no EOL
    arr = new byte[]{0, 1, 2};
    Assert.assertEquals(-1, SdcStreamFragmenter.findEndOfLastLineBeforeLimit(arr, 3));

    // EOL
    arr = new byte[]{0, '\n', 2};
    Assert.assertEquals(2, SdcStreamFragmenter.findEndOfLastLineBeforeLimit(arr, 3));
  }


  @Test
  public void testExtract() throws IOException {
    // empty input
    InputStream is = new ByteArrayInputStream(new byte[0]);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] arr = SdcStreamFragmenter.extract(is, baos, 5);
    Assert.assertNull(arr);
    Assert.assertEquals(0, baos.size());

    // input size < limit
    is = new ByteArrayInputStream(new byte[]{1, 2, '\n'});
    baos = new ByteArrayOutputStream();
    arr = SdcStreamFragmenter.extract(is, baos, 5);
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, '\n'}, arr);
    Assert.assertEquals(0, baos.size());

    // input size == limit, EOL at EOF
    is = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, '\n'});
    baos = new ByteArrayOutputStream();
    arr = SdcStreamFragmenter.extract(is, baos, 5);
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, 3, 4, '\n'}, arr);
    Assert.assertEquals(0, baos.size());

    // input size == limit, EOL before EOF
    is = new ByteArrayInputStream(new byte[]{1, 2, '\n', 4, 5});
    baos = new ByteArrayOutputStream();
    arr = SdcStreamFragmenter.extract(is, baos, 5);
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, '\n'}, arr);
    Assert.assertArrayEquals(new byte[]{4, 5}, baos.toByteArray());

    // input size > limit
    is = new ByteArrayInputStream(new byte[]{1, 2, '\n', 4, 5, '\n'});
    baos = new ByteArrayOutputStream();
    arr = SdcStreamFragmenter.extract(is, baos, 5);
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, '\n'}, arr);
    Assert.assertArrayEquals(new byte[]{4, 5}, baos.toByteArray());
    arr = SdcStreamFragmenter.extract(is, baos, 5);
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 4, 5, '\n'}, arr);
    Assert.assertEquals(0, baos.size());
  }

  @Test(expected = IOException.class)
  public void testExtractMessageExceededLimit() throws IOException {
    InputStream is = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5, '\n'});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    SdcStreamFragmenter.extract(is, baos, 5);
  }

  @Test(expected = IOException.class)
  public void testFragmentEmptyInput() throws IOException {
    InputStream is = new ByteArrayInputStream(new byte[]{});
    SdcStreamFragmenter.fragment(is, 5, 100);
  }

  @Test(expected = IOException.class)
  public void testFragmentWrongMagicByte() throws IOException {
    InputStream is = new ByteArrayInputStream(new byte[]{1});
    SdcStreamFragmenter.fragment(is, 5, 100);
  }

  @Test(expected = IOException.class)
  public void testFragmentDataSizeExceeded() throws IOException {
    InputStream is = new ByteArrayInputStream(new byte[]{
        SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, 3, '\n', 1, 2, 3, 4, '\n', 1, 2, 3
    });
    SdcStreamFragmenter.fragment(is, 6, 10);
  }

  @Test
  public void testFragment() throws IOException {
    InputStream is = new ByteArrayInputStream(new byte[]{
        SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, '\n', 1, 2, 3, '\n', 1, 2, 3, '\n', 1, 2, '\n'
    });
    List<byte[]> fragments = SdcStreamFragmenter.fragment(is, 6, 100);
    Assert.assertEquals(4, fragments.size());
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, '\n'}, fragments.get(0));
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, 3, '\n'}, fragments.get(1));
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, 3, '\n',}, fragments.get(2));
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, '\n'}, fragments.get(3));

    is = new ByteArrayInputStream(new byte[]{
        SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, '\n', 1, 2, '\n', 1, 2, '\n', 1, '\n'
    });
    fragments = SdcStreamFragmenter.fragment(is, 8, 100);
    Assert.assertEquals(2, fragments.size());
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, '\n', 1, 2, '\n'}, fragments.get(0));
    Assert.assertArrayEquals(new byte[]{SdcStreamFragmenter.JSON1_MAGIC_NUMBER, 1, 2, '\n', 1, '\n'}, fragments.get(1));
  }

  @Test
  public void testFragmentWithSDCData() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Source.Context context =
        ContextInfoCreator.createSourceContext("foo", false, OnRecordError.TO_ERROR, Arrays.asList("a"));
    ContextExtensions ext = (ContextExtensions) context;
    RecordWriter rw = ext.createRecordWriter(baos);
    Record r1 = RecordCreator.create();
    r1.set(Field.create(true));
    Record r2 = RecordCreator.create();
    r2.set(Field.create(1));
    Record r3 = RecordCreator.create();
    r3.set(Field.create("a"));
    List<Record> records = Arrays.asList(r1, r2 , r3);
    for (Record record : records) {
      rw.write(record);
    }
    rw.close();
    List<byte[]> fragments = SdcStreamFragmenter.fragment(new ByteArrayInputStream(baos.toByteArray()), 800, 2000);
    Assert.assertEquals(2, fragments.size());
    List<Record> got = new ArrayList<>();
    for (byte[] fragment : fragments) {
      InputStream is = new ByteArrayInputStream(fragment);
      RecordReader rr = ext.createRecordReader(is, 0, 500);
      Record r = rr.readRecord();
      while (r != null) {
        got.add(r);
        r = rr.readRecord();
      }
      rr.close();
    }
    Assert.assertEquals(records, got);
  }

}
