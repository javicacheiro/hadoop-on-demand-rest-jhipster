'use strict';

angular.module('hadooprestApp')
    .controller('SshKeyDetailController', function ($scope, $stateParams, SshKey) {
        $scope.sshKey = {};
        $scope.load = function (id) {
            SshKey.get({id: id}, function(result) {
              $scope.sshKey = result;
            });
        };
        $scope.load($stateParams.id);
    });
