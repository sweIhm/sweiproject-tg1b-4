var app = angular.module('IUA_new', ['ngMaterial']);

app.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('blue')
        .accentPalette('red');
});

app.controller('IUACtrl', function($scope, $mdSidenav) {

    // Check if user call's site from http and redirect to https if true.
    /*if (location.protocol !== 'https:')
    {
        alert("This site only works in https:</br>Click okay to get redirected to https:");
        location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
    }*/

    $scope.title1 = 'Button';
    $scope.title4 = 'Warn';
    $scope.isDisabled = true;
    $scope.googleUrl = 'http://google.com';

    $scope.toggleLeftSidebar = function() {
        $mdSidenav('left_Sidebar').toggle();
    };

    $scope.search_form_submit = function() {
        if (document.getElementById('search_input').value !== "") {
            alert("Works");
            document.getElementById('search_form').reset();
        }
    };

    $scope.open_add_activity_dialog = function() {

    };

    $scope.open_edit_activity_dialog = function() {

    };

    $scope.open_login_dialog = function() {

    };

    $scope.open_registration_dialog = function() {

    };

    $scope.open_activity_details_dialog = function() {

    };
});

app.controller('AddActivityDialogCtrl', function($scope) {

});

app.controller('EditActivityDialogCtrl', function($scope) {

});

app.controller('LoginDialogCtrl', function($scope) {

});

app.controller('RegistrationDialogCtrl', function($scope) {

});

app.controller('activityDetailsDialogCtrl', function($scope) {

});