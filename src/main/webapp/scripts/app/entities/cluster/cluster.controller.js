'use strict';

angular.module('hadooprestApp')
    .controller('ClusterController', function ($scope, Cluster) {
        $scope.clusters = [];
        $scope.loadAll = function() {
            Cluster.query(function(result) {
               $scope.clusters = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Cluster.update($scope.cluster,
                function () {
                    $scope.loadAll();
                    $('#saveClusterModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Cluster.get({id: id}, function(result) {
                $scope.cluster = result;
                $('#saveClusterModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Cluster.get({id: id}, function(result) {
                $scope.cluster = result;
                $('#deleteClusterConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Cluster.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteClusterConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.cluster = {clustername: null, version: null, size: null, replication: null, blocksize: null, submitTime: null, stopTime: null, exitStatus: null, status: null, username: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
