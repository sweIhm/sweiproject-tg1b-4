var app = angular.module('IUA_new', ['ngMaterial']);

app.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('blue')
        .accentPalette('red');
});

app.controller('IUACtrl', function($scope, $mdSidenav, $mdDialog) {

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

    $scope.open_add_activity_dialog = function(ev) {
        $mdDialog.show({
            controller: addActivityDialogCtrl,
            templateUrl: 'addActivityDialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        })
            .then(function() {
                //Load activities
            });
    };

    $scope.open_edit_activity_dialog = function() {

    };

    $scope.open_login_dialog = function(ev) {
        $mdDialog.show({
            controller: loginDialogCtrl,
            templateUrl: 'loginDialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        })
            .then(function() {
                //Load activities
            });
    };

    $scope.open_registration_dialog = function(ev) {

    };

    $scope.open_activity_details_dialog = function() {

    };

    function addActivityDialogCtrl($scope, $mdDialog) {
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.answer = function(answer) {
            $mdDialog.hide(answer);
        };
    }

    function loginDialogCtrl($scope, $mdDialog) {
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.login = function(login) {
            alert($scope.login.email);
            //Handle login
        };
        $scope.forgot_password = function () {
            //Handle forgot password
        }
    }
});

app.controller('EditActivityDialogCtrl', function($scope) {

});

app.controller('LoginDialogCtrl', function($scope) {

});

app.controller('RegistrationDialogCtrl', function($scope) {

});

app.controller('activityDetailsDialogCtrl', function($scope) {

});