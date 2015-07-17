/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.dc.restapi;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.streamsets.pipeline.api.Batch;
import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ExecutionMode;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseSource;
import com.streamsets.pipeline.api.base.BaseTarget;
import com.streamsets.pipeline.api.impl.ErrorMessage;
import com.streamsets.pipeline.config.ConfigDefinition;
import com.streamsets.pipeline.config.StageDefinition;
import com.streamsets.pipeline.config.StageLibraryDefinition;
import com.streamsets.pipeline.config.StageType;
import com.streamsets.pipeline.el.ElConstantDefinition;
import com.streamsets.pipeline.el.ElFunctionDefinition;
import com.streamsets.pipeline.main.RuntimeInfo;
import com.streamsets.pipeline.prodmanager.PipelineManagerException;
import com.streamsets.pipeline.prodmanager.PipelineState;
import com.streamsets.pipeline.prodmanager.StandalonePipelineManagerTask;
import com.streamsets.pipeline.prodmanager.State;
import com.streamsets.pipeline.record.RecordImpl;
import com.streamsets.pipeline.runner.PipelineRuntimeException;
import com.streamsets.pipeline.snapshotstore.SnapshotStatus;
import com.streamsets.pipeline.stagelibrary.StageLibraryTask;
import com.streamsets.pipeline.store.PipelineStoreException;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;

import javax.inject.Singleton;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class TestUtil {

  private static final String PIPELINE_NAME = "myPipeline";
  private static final String PIPELINE_REV = "2.0";
  private static final String DEFAULT_PIPELINE_REV = "0";
  private static final String SNAPSHOT_NAME = "snapshot";

  /**
   * Mock source implementation
   */
  public static class TSource extends BaseSource {
    public boolean inited;
    public boolean destroyed;

    @Override
    protected List<ConfigIssue> init() {
      List<ConfigIssue> issues = super.init();
      inited = true;
      return issues;
    }

    @Override
    public void destroy() {
      destroyed = true;
    }

    @Override
    public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
      return null;
    }
  }

  /**
   * Mock target implementation
   */
  public static class TTarget extends BaseTarget {
    public boolean inited;
    public boolean destroyed;

    @Override
    protected List<ConfigIssue> init() {
      List<ConfigIssue> issues = super.init();
      inited = true;
      return issues;
    }

    @Override
    public void destroy() {
      destroyed = true;
    }
    @Override
    public void write(Batch batch) throws StageException {
    }
  }

  private static final StageLibraryDefinition MOCK_LIB_DEF =
      new StageLibraryDefinition(TestUtil.class.getClassLoader(), "mock", "MOCK", new Properties());

  @SuppressWarnings("unchecked")
  /**
   *
   * @return Mock stage library implementation
   */
  public static StageLibraryTask createMockStageLibrary() {
    StageLibraryTask lib = Mockito.mock(StageLibraryTask.class);
    List<ConfigDefinition> configDefs = new ArrayList<>();
    ConfigDefinition configDef = new ConfigDefinition("string", ConfigDef.Type.STRING, "l1", "d1", "--", true, "g",
        "stringVar", null, "", new ArrayList<>(), 0, Collections.<ElFunctionDefinition>emptyList(),
      Collections.<ElConstantDefinition>emptyList(), Long.MIN_VALUE, Long.MAX_VALUE, "text/plain", 0, Collections.<Class> emptyList(),
      ConfigDef.Evaluation.IMPLICIT, null);
    configDefs.add(configDef);
    configDef = new ConfigDefinition("int", ConfigDef.Type.NUMBER, "l2", "d2", "-1", true, "g", "intVar", null, "",
      new ArrayList<>(), 0, Collections.<ElFunctionDefinition>emptyList(),
      Collections.<ElConstantDefinition>emptyList(), Long.MIN_VALUE, Long.MAX_VALUE, "text/plain", 0, Collections.<Class> emptyList(),
      ConfigDef.Evaluation.IMPLICIT, null);
    configDefs.add(configDef);
    configDef = new ConfigDefinition("long", ConfigDef.Type.NUMBER, "l3", "d3", "-2", true, "g", "longVar", null, "",
      new ArrayList<>(), 0, Collections.<ElFunctionDefinition>emptyList(),
      Collections.<ElConstantDefinition>emptyList(), Long.MIN_VALUE, Long.MAX_VALUE, "text/plain", 0, Collections.<Class> emptyList(),
      ConfigDef.Evaluation.IMPLICIT, null);
    configDefs.add(configDef);
    configDef = new ConfigDefinition("boolean", ConfigDef.Type.BOOLEAN, "l4", "d4", "false", true, "g", "booleanVar",
      null, "", new ArrayList<>(), 0, Collections.<ElFunctionDefinition>emptyList(),
      Collections.<ElConstantDefinition>emptyList(), Long.MIN_VALUE, Long.MAX_VALUE, "text/plain", 0, Collections.<Class> emptyList(),
      ConfigDef.Evaluation.IMPLICIT, null);
    configDefs.add(configDef);
    StageDefinition sourceDef = new StageDefinition(
        MOCK_LIB_DEF, false, TSource.class, "source", 1, "label", "description",
        StageType.SOURCE, false, true, true, configDefs, null/*raw source definition*/, "", null, false ,1,
        null, Arrays.asList(ExecutionMode.CLUSTER, ExecutionMode.STANDALONE), false);
    StageDefinition targetDef = new StageDefinition(
        MOCK_LIB_DEF, false, TTarget.class, "target", 1, "label", "description",
        StageType.TARGET, false, true, true, Collections.<ConfigDefinition>emptyList(), null/*raw source definition*/,
        "TargetIcon.svg", null, false, 0, null, Arrays.asList(ExecutionMode.CLUSTER,
                                                              ExecutionMode.STANDALONE), false);
    Mockito.when(lib.getStage(Mockito.eq("library"), Mockito.eq("source"), Mockito.eq(false)))
           .thenReturn(sourceDef);
    Mockito.when(lib.getStage(Mockito.eq("library"), Mockito.eq("target"), Mockito.eq(false)))
           .thenReturn(targetDef);

    List<StageDefinition> stages = new ArrayList<>(2);
    stages.add(sourceDef);
    stages.add(targetDef);
    Mockito.when(lib.getStages()).thenReturn(stages);
    return lib;
  }

  public static class StageLibraryTestInjector implements Factory<StageLibraryTask> {

    public StageLibraryTestInjector() {
    }

    @Singleton
    @Override
    public StageLibraryTask provide() {
      return createMockStageLibrary();
    }

    @Override
    public void dispose(StageLibraryTask stageLibrary) {
    }
  }

  public static class URITestInjector implements Factory<URI> {
    @Override
    public URI provide() {
      try {
        return new URI("URIInjector");
      } catch (URISyntaxException e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    public void dispose(URI uri) {
    }

  }

  public static class PrincipalTestInjector implements Factory<Principal> {

    @Override
    public Principal provide() {
      return new Principal() {
        @Override
        public String getName() {
          return "nobody";
        }
      };
    }

    @Override
    public void dispose(Principal principal) {
    }

  }

  static class PipelineManagerTestInjector implements Factory<StandalonePipelineManagerTask> {

    public PipelineManagerTestInjector() {
    }

    @Singleton
    @Override
    public StandalonePipelineManagerTask provide() {

      StandalonePipelineManagerTask pipelineManager = Mockito.mock(StandalonePipelineManagerTask.class);
      try {
        Mockito.when(pipelineManager.startPipeline(PIPELINE_NAME, PIPELINE_REV)).thenReturn(new PipelineState(
          PIPELINE_NAME, "2.0", State.RUNNING, "The pipeline is now running", System.currentTimeMillis(), null, null));
      } catch (PipelineManagerException | StageException | PipelineRuntimeException | PipelineStoreException e) {
        e.printStackTrace();
      }

      try {
        Mockito.when(pipelineManager.stopPipeline(false)).thenReturn(
          new PipelineState(PIPELINE_NAME, PIPELINE_REV, State.STOPPED, "The pipeline is not running",
                            System.currentTimeMillis(), null, null));
      } catch (PipelineManagerException e) {
        e.printStackTrace();
      }

      Mockito.when(pipelineManager.getPipelineState()).thenReturn(new PipelineState(PIPELINE_NAME, PIPELINE_REV,
                                                                                    State.STOPPED
        , "Pipeline is not running", System.currentTimeMillis(), null, null));

      try {
        Mockito.when(pipelineManager.getSnapshot(PIPELINE_NAME, DEFAULT_PIPELINE_REV, SNAPSHOT_NAME))
          .thenReturn(getClass().getClassLoader().getResourceAsStream("snapshot.json"))
          .thenReturn(null);
      } catch (PipelineManagerException e) {
        e.printStackTrace();
      }

      Mockito.when(pipelineManager.getSnapshotStatus(SNAPSHOT_NAME)).thenReturn(new SnapshotStatus(false, true));

      Mockito.when(pipelineManager.getMetrics()).thenReturn(new MetricRegistry());

      List<PipelineState> states = new ArrayList<>();
      states.add(new PipelineState(PIPELINE_NAME, "1", State.STOPPED, "", System.currentTimeMillis(), null, null));
      states.add(new PipelineState(PIPELINE_NAME, "1", State.RUNNING, "", System.currentTimeMillis(), null, null));
      states.add(new PipelineState(PIPELINE_NAME, "1", State.STOPPED, "", System.currentTimeMillis(), null, null));
      try {
        Mockito.when(pipelineManager.getHistory(PIPELINE_NAME, DEFAULT_PIPELINE_REV, false)).thenReturn(states);
      } catch (PipelineManagerException e) {
        e.printStackTrace();
      }

      Mockito.doNothing().when(pipelineManager).deleteSnapshot(PIPELINE_NAME, PIPELINE_REV, SNAPSHOT_NAME);

      Record r = new RecordImpl("a", "b", "c".getBytes(), "d");
      try {
        Mockito.when(pipelineManager.getErrorRecords("myProcessorStage", 100)).thenReturn(
          ImmutableList.of(r));
      } catch (PipelineManagerException e) {
        e.printStackTrace();
      }

      ErrorMessage em = new ErrorMessage("a", "b", 2L);
      try {
        Mockito.when(pipelineManager.getErrorMessages("myProcessorStage", 100)).thenReturn(
          ImmutableList.of(em));
      } catch (PipelineManagerException e) {
        e.printStackTrace();
      }

      try {
        Mockito.doNothing().when(pipelineManager).resetOffset(PIPELINE_NAME, PIPELINE_REV);
      } catch (PipelineManagerException e) {
        e.printStackTrace();
      }

      return pipelineManager;
    }

    @Override
    public void dispose(StandalonePipelineManagerTask pipelineManagerTask) {
    }
  }


  public static class RuntimeInfoTestInjector implements Factory<RuntimeInfo> {
    @Singleton
    @Override
    public RuntimeInfo provide() {
      RuntimeInfo runtimeInfo = Mockito.mock(RuntimeInfo.class);
      Mockito.when(runtimeInfo.getExecutionMode()).thenReturn(RuntimeInfo.ExecutionMode.STANDALONE);
      return runtimeInfo;
    }

    @Override
    public void dispose(RuntimeInfo runtimeInfo) {
    }

  }

  public static class RuntimeInfoTestInjectorForSlaveMode implements Factory<RuntimeInfo> {
    @Singleton
    @Override
    public RuntimeInfo provide() {
      RuntimeInfo runtimeInfo = Mockito.mock(RuntimeInfo.class);
      Mockito.when(runtimeInfo.getExecutionMode()).thenReturn(RuntimeInfo.ExecutionMode.SLAVE);
      return runtimeInfo;
    }

    @Override
    public void dispose(RuntimeInfo runtimeInfo) {
    }

  }

}