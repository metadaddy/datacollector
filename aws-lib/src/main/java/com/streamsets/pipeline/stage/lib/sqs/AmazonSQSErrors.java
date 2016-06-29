package com.streamsets.pipeline.stage.lib.sqs;

import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.GenerateResourceBundle;

@GenerateResourceBundle
public enum AmazonSQSErrors implements ErrorCode {
  SKELETON_00("A configuration is invalid because: {}"),
  SKELETON_01("Specific reason writing record failed: {}"),
      ;
  private final String msg;

  AmazonSQSErrors(String msg) {
    this.msg = msg;
  }

  /** {@inheritDoc} */
  @Override
  public String getCode() {
    return name();
  }

  /** {@inheritDoc} */
  @Override
  public String getMessage() {
    return msg;
  }
}
