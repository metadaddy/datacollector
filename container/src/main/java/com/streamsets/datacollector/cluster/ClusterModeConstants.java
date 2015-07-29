/**
 * (c) 2015 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.datacollector.cluster;

import com.google.common.collect.ImmutableSet;

public class ClusterModeConstants {
  public static final String API_LIB = "api-lib";
  public static final String CONTAINER_LIB = "container-lib";
  public static final String STREAMSETS_LIBS = "streamsets-libs";
  public static final String USER_LIBS = "user-libs";

  public static final String NUM_EXECUTORS_KEY = "num-executors";
  public static final String CLUSTER_SOURCE_NAME = "cluster.source.name";
  public static final String CLUSTER_PIPELINE_NAME = "cluster.pipeline.name";
  public static final String CLUSTER_PIPELINE_REV = "cluster.pipeline.rev";
  public static final String CLUSTER_PIPELINE_USER = "cluster.pipeline.user";

  public static final String SPARK_KAFKA_JAR_PREFIX = "spark-streaming-kafka";
  public static final String CLUSTER_SOURCE_BATCHMODE = "cluster.source.batchmode";

}