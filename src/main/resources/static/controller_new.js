var app = angular.module('IUA_new', ['ngMaterial']);

app.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('blue')
        .accentPalette('red');
});

function loadActivities ($scope, $http){
    $http({
        method : 'GET',
        url: (window.location.hostname === 'localhost' ?
            'http://localhost:8080/activity' :
            'https://iua.herokuapp.com/activity')
    }).then(function (response) {
        $scope.activities = response.data;
    });
}

app.controller('IUACtrl', function($scope, $http, $mdSidenav, $mdDialog) {

    // Check if user call's site from http and redirect to https if true.
    /*if (location.protocol !== 'https:')
    {
        alert("This site only works in https:</br>Click okay to get redirected to https:");
        location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
    }*/

    loadActivities($scope, $http);

    $scope.title1 = 'Button';
    $scope.title4 = 'Warn';
    $scope.isDisabled = true;
    $scope.googleUrl = 'http://google.com';

    $scope.toggleLeftSidebar = function() {
        $mdSidenav('left_Sidebar').toggle();
    };

    $scope.search_form_submit = function() {
        if (document.getElementById('search_input').value !== "") {
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
        }).finally(function() {
            alert("hello");
            loadActivities($scope, $http);
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
                //...
            });
    };

    $scope.open_registration_dialog = function(ev) {

    };

    $scope.open_activity_details_dialog = function() {

    };

    function addActivityDialogCtrl($scope, $mdDialog, $http) {
        $scope.cancel = function() {
            $mdDialog.cancel("blub");
        };
        $scope.add = function(activity) {
            var postRequest = {
                method : 'POST',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/activity' :
                    'https://iua.herokuapp.com/activity'),
                data: {
                    title: $scope.activity.title,
                    text: $scope.activity.text,
                    tags: $scope.activity.tags
                }
            };
            $http(postRequest).then(function (response) {
                $scope.activities = response.data;
            }).then(function () {
                $scope.cancel();
            });
        };
    }

    function loginDialogCtrl($scope, $mdDialog) {
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