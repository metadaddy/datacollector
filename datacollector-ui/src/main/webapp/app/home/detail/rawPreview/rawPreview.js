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

/**
 * Controller for Raw Preview.
 */

angular
  .module('dataCollectorApp.home')

  .controller('RawPreviewController', function ($scope, $rootScope, $q, $modal, _, api) {

    angular.extend($scope, {
      rawDataCodemirrorOptions: {
        mode: {
          name: 'application/json'
        },
        readOnly: 'nocursor',
        lineNumbers: true
      },

      /**
       * Raw Source Preview
       */
      rawSourcePreview: function() {
        api.pipelineAgent.rawSourcePreview($scope.activeConfigInfo.name, 0, $scope.detailPaneConfig.uiInfo.rawSource.configuration)
          .success(function(data) {
            $rootScope.common.errors = [];
            $scope.rawSourcePreviewData = data ? data.previewData : '';

            $scope.refreshCodemirror = true;
            $timeout(function () {
              $scope.refreshCodemirror = false;
            }, 100);
          })
          .error(function(data, status, headers, config) {
            $rootScope.common.errors = [data];
          });
      }
    });

  });