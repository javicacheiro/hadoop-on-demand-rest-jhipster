'use strict';

angular.module('hadooprestApp')
    .controller('IpDetailController', function ($scope, $stateParams, Ip, User) {
        $scope.ip = {};
        $scope.load = function (id) {
            Ip.get({id: id}, function(result) {
              $scope.ip = result;
            });
        };
        $scope.load($stateParams.id);
    });
