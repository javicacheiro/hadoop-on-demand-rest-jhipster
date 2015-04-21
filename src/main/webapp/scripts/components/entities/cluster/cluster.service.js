'use strict';

angular.module('hadooprestApp')
    .factory('Cluster', function ($resource) {
        return $resource('api/clusters/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.submitTime = new Date(data.submitTime);
                    data.stopTime = new Date(data.stopTime);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
