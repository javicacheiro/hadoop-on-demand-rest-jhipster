'use strict';

angular.module('hadooprestApp')
    .controller('IpController', function ($scope, Ip, User, ParseLinks) {
        $scope.ips = [];
        $scope.users = User.query();
        $scope.page = 1;
        $scope.loadAll = function() {
            Ip.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.ips = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Ip.update($scope.ip,
                function () {
                    $scope.loadAll();
                    $('#saveIpModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Ip.get({id: id}, function(result) {
                $scope.ip = result;
                $('#saveIpModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Ip.get({id: id}, function(result) {
                $scope.ip = result;
                $('#deleteIpConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Ip.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteIpConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.ip = {address: null, enabled: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
