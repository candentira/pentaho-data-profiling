/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.plugin.integration.extension;

import com.pentaho.plugin.util.DataSourceFieldValueCreator;
import com.pentaho.profiling.api.StreamingProfile;
import com.pentaho.profiling.api.action.ProfileActionException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.RowListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryan on 5/13/15.
 */
public class ProfileTransformationRowListener implements RowListener {
  public static final String UNABLE_TO_PROCESS_RECORD = "Unable to process record ";
  private final LogChannelInterface logChannelInterface;
  private final StreamingProfile streamingProfile;
  private final List<DataSourceFieldValue> list;
  private final DataSourceFieldValueCreator dataSourceFieldValueCreator;

  public ProfileTransformationRowListener( LogChannelInterface logChannelInterface,
                                           StreamingProfile streamingProfile ) {
    this( logChannelInterface, streamingProfile, new DataSourceFieldValueCreator(),
      new ArrayList<DataSourceFieldValue>() );
  }

  public ProfileTransformationRowListener( LogChannelInterface logChannelInterface,
                                           StreamingProfile streamingProfile,
                                           DataSourceFieldValueCreator dataSourceFieldValueCreator,
                                           List<DataSourceFieldValue> list ) {
    this.logChannelInterface = logChannelInterface;
    this.streamingProfile = streamingProfile;
    this.dataSourceFieldValueCreator = dataSourceFieldValueCreator;
    this.list = list;
  }

  @Override public void rowReadEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {

  }

  @Override public synchronized void rowWrittenEvent( RowMetaInterface rowMeta, Object[] row )
    throws KettleStepException {
    list.clear();
    dataSourceFieldValueCreator.createDataSourceFields( list, rowMeta, row );
    try {
      streamingProfile.processRecord( list );
    } catch ( ProfileActionException e ) {
      logChannelInterface.logError( UNABLE_TO_PROCESS_RECORD + list, e );
    }
  }

  @Override public void errorRowWrittenEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {

  }
}