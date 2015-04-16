'use strict';

angular.module('hadooprestApp')
    .factory('Ip', function ($resource) {
        return $resource('api/ips/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
