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
 * Object limitTo filter
 */

angular.module('dataCollectorApp.filters')
  .filter('objLimitTo', function() {
    return function(obj, limit){
      if(obj === null) {
        return [];
      }

      var keys = Object.keys(obj);
      if(keys.length < 1){
        return [];
      }
      var ret = {},
        count = 0;

      keys.sort();

      angular.forEach(keys, function(key){
        if(count >= limit){
          return false;
        }
        ret[key] = obj[key];
        count++;
      });

      return ret;
    };
  });