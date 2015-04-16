'use strict';

angular.module('hadooprestApp')
    .controller('KeyController', function ($scope, Key, User) {
        $scope.keys = [];
        $scope.users = User.query();
        $scope.loadAll = function() {
            Key.query(function(result) {
               $scope.keys = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Key.update($scope.key,
                function () {
                    $scope.loadAll();
                    $('#saveKeyModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Key.get({id: id}, function(result) {
                $scope.key = result;
                $('#saveKeyModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Key.get({id: id}, function(result) {
                $scope.key = result;
                $('#deleteKeyConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Key.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteKeyConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.key = {type: null, pubkey: null, enabled: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
