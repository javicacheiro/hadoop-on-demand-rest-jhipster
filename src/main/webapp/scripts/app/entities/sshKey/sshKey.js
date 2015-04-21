'use strict';

angular.module('hadooprestApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sshKey', {
                parent: 'entity',
                url: '/sshKey',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hadooprestApp.sshKey.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sshKey/sshKeys.html',
                        controller: 'SshKeyController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sshKey');
                        return $translate.refresh();
                    }]
                }
            })
            .state('sshKeyDetail', {
                parent: 'entity',
                url: '/sshKey/:id',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'hadooprestApp.sshKey.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sshKey/sshKey-detail.html',
                        controller: 'SshKeyDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sshKey');
                        return $translate.refresh();
                    }]
                }
            });
    });
