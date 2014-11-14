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
package com.streamsets.pipeline.main;


import com.streamsets.pipeline.http.WebServerTask;
import com.streamsets.pipeline.stagelibrary.StageLibrary;
import com.streamsets.pipeline.store.PipelineStoreTask;
import com.streamsets.pipeline.task.AbstractTask;
import com.streamsets.pipeline.state.PipelineManager;

import javax.inject.Inject;

public class PipelineTask extends AbstractTask {
  private final StageLibrary library;
  private final PipelineStoreTask store;
  private final WebServerTask webServer;
  private final PipelineManager stateMgr;

  @Inject
  public PipelineTask(StageLibrary library, PipelineStoreTask store, WebServerTask webServer, PipelineManager stateMgr) {
    super("pipeline");
    this.library = library;
    this.store = store;
    this.webServer = webServer;
    this.stateMgr = stateMgr;
  }

  @Override
  protected void initTask() {
    library.init();
    store.init();
    webServer.init();
    stateMgr.init();
  }

  @Override
  protected void runTask() {
    library.run();
    store.run();
    webServer.run();
  }

  @Override
  protected void stopTask() {
    webServer.stop();
    store.stop();
    stateMgr.destroy();
    library.stop();
  }
}
