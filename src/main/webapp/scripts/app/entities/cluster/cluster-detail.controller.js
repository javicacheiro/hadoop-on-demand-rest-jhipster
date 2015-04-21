'use strict';

angular.module('hadooprestApp')
    .controller('ClusterDetailController', function ($scope, $stateParams, Cluster) {
        $scope.cluster = {};
        $scope.load = function (id) {
            Cluster.get({id: id}, function(result) {
              $scope.cluster = result;
            });
        };
        $scope.load($stateParams.id);
    });
