'use strict';

angular.module('hadooprestApp')
    .controller('SshKeyController', function ($scope, SshKey) {
        $scope.sshKeys = [];
        $scope.loadAll = function() {
            SshKey.query(function(result) {
               $scope.sshKeys = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            SshKey.update($scope.sshKey,
                function () {
                    $scope.loadAll();
                    $('#saveSshKeyModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            SshKey.get({id: id}, function(result) {
                $scope.sshKey = result;
                $('#saveSshKeyModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            SshKey.get({id: id}, function(result) {
                $scope.sshKey = result;
                $('#deleteSshKeyConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            SshKey.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteSshKeyConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.sshKey = {type: null, pubkey: null, enabled: null, username: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
