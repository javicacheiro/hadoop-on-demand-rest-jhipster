'use strict';

angular.module('hadooprestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('key', {
                parent: 'entity',
                url: '/key',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hadooprestApp.key.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/key/keys.html',
                        controller: 'KeyController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('key');
                        return $translate.refresh();
                    }]
                }
            })
            .state('keyDetail', {
                parent: 'entity',
                url: '/key/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hadooprestApp.key.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/key/key-detail.html',
                        controller: 'KeyDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('key');
                        return $translate.refresh();
                    }]
                }
            });
    });
