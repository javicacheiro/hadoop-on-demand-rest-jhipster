'use strict';

angular.module('hadooprestApp')
    .factory('Password', function ($resource) {
        return $resource('api/account/change_password', {}, {
        });
    });
