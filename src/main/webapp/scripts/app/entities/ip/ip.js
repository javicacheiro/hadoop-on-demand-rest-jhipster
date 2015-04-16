'use strict';

angular.module('hadooprestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ip', {
                parent: 'entity',
                url: '/ip',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hadooprestApp.ip.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ip/ips.html',
                        controller: 'IpController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ip');
                        return $translate.refresh();
                    }]
                }
            })
            .state('ipDetail', {
                parent: 'entity',
                url: '/ip/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hadooprestApp.ip.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ip/ip-detail.html',
                        controller: 'IpDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('ip');
                        return $translate.refresh();
                    }]
                }
            });
    });
