/**
 * Licensed to the Apache Software Foundation (ASF) under one
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
package com.streamsets.datacollector.runner;

import com.streamsets.datacollector.main.RuntimeInfo;
import com.streamsets.datacollector.runner.FullPipeBatch;
import com.streamsets.datacollector.runner.Observer;
import com.streamsets.datacollector.runner.ObserverPipe;
import com.streamsets.datacollector.runner.PipeBatch;
import com.streamsets.datacollector.runner.Pipeline;
import com.streamsets.datacollector.runner.PipelineRunner;

import com.streamsets.datacollector.util.Configuration;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

public class TestObserverPipe {

  @Test
  @SuppressWarnings("unchecked")
  public void testNullObserver() throws Exception {
    PipelineRunner pipelineRunner = Mockito.mock(PipelineRunner.class);
    Mockito.when(pipelineRunner.getRuntimeInfo()).thenReturn(Mockito.mock(RuntimeInfo.class));
    Pipeline pipeline = new Pipeline.Builder(MockStages.createStageLibrary(), new Configuration(), "name", "name", "0",
                                             MockStages.createPipelineConfigurationSourceTarget()).build(pipelineRunner);
    ObserverPipe pipe = (ObserverPipe) pipeline.getPipes()[1];
    PipeBatch pipeBatch = Mockito.mock(FullPipeBatch.class);
    pipe.process(pipeBatch);
    Mockito.verify(pipeBatch, Mockito.times(1)).moveLane(Mockito.anyString(), Mockito.anyString());
    Mockito.verifyNoMoreInteractions(pipeBatch);
  }

  @SuppressWarnings("unchecked")
  private void testObserver(boolean observing) throws Exception {
    PipelineRunner pipelineRunner = Mockito.mock(PipelineRunner.class);
    Mockito.when(pipelineRunner.getRuntimeInfo()).thenReturn(Mockito.mock(RuntimeInfo.class));
    Observer observer = Mockito.mock(Observer.class);
    Mockito.when(observer.isObserving(Mockito.any(List.class))).thenReturn(observing);
    Pipeline pipeline = new Pipeline.Builder(MockStages.createStageLibrary(), new Configuration(), "name", "name", "0",
                                             MockStages.createPipelineConfigurationSourceTarget()).setObserver(observer)
                                             .build(pipelineRunner);
    ObserverPipe pipe = (ObserverPipe) pipeline.getPipes()[1];
    PipeBatch pipeBatch = Mockito.mock(FullPipeBatch.class);
    pipe.process(pipeBatch);
    Mockito.verify(observer, Mockito.times(1)).isObserving(Mockito.any(List.class));
    Mockito.verify(pipeBatch, Mockito.times(1)).moveLane(Mockito.anyString(), Mockito.anyString());
    if (observing) {
      Mockito.verify(observer, Mockito.times(1)).observe(Mockito.eq(pipe), Mockito.anyMap());
      Mockito.verify(pipeBatch, Mockito.times(1)).getLaneOutputRecords(Mockito.anyList());
    } else {
      Mockito.verifyNoMoreInteractions(observer);
      Mockito.verifyNoMoreInteractions(pipeBatch);
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testObserverOff() throws Exception {
    testObserver(false);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testObserverOn() throws Exception {
    testObserver(true);
  }

}