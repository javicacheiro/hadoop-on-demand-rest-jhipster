'use strict';

angular.module('hadooprestApp')
    .factory('SshKey', function ($resource) {
        return $resource('api/sshKeys/:id', {}, {
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
