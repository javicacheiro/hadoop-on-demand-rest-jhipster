'use strict';

angular.module('hadooprestApp')
    .controller('KeyDetailController', function ($scope, $stateParams, Key, User) {
        $scope.key = {};
        $scope.load = function (id) {
            Key.get({id: id}, function(result) {
              $scope.key = result;
            });
        };
        $scope.load($stateParams.id);
    });
