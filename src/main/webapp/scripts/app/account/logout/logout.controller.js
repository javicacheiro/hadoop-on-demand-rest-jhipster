'use strict';

angular.module('hadooprestApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
