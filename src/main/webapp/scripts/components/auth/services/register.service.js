'use strict';

angular.module('hadooprestApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


