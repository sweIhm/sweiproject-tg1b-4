var app = angular.module('IUA_new', ['ngMaterial']);

app.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('blue')
        .accentPalette('red');
});

app.controller('IUACtrl', function($scope, $timeout, $mdSidenav) {
    $scope.title1 = 'Button';
    $scope.title4 = 'Warn';
    $scope.isDisabled = true;
    $scope.googleUrl = 'http://google.com';

    $scope.toggleLeftSidebar = function() {
        $mdSidenav('left_Sidebar').toggle();
    };

    $scope.expand_search_field = function () {
        document.getElementById("searchInput").style.visibility = "visible";
        document.getElementById("searchInput").style.width = "50%";
    }
});