var app = angular.module('IUA_new', ['ngMaterial']);

var heroku_address =  'https://iua.herokuapp.com/';

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
            heroku_address + '/activity')
    }).then(function (response) {
        $scope.activities = response.data;
    });
}

app.controller('IUACtrl', function($scope, $http, $mdSidenav, $mdDialog) {

    // Check if user call's site from http and redirect to https if true.
    /*if (location.protocol !== 'https:')
    {
        alert("This site only works in https:. Click ok to get redirected to https:");
        location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
    }*/

    loadActivities($scope, $http);

    $scope.toggleLeftSidebar = function() {
        $mdSidenav('left_Sidebar').toggle();
    };

    $scope.search_form_submit = function() {
        if ($scope.search_text_field.value !== "") {
            $scope.search_text_field = '';
        }
    };

    $scope.open_add_activity_dialog = function(ev) {
        $mdDialog.show({
            controller: addActivityDialogCtrl,
            templateUrl: './dialogs/addActivityDialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        }).finally(function() {
            loadActivities($scope, $http);
        });
    };

    $scope.open_edit_activity_dialog = function(activity, ev) {
        ev.stopPropagation();
        $mdDialog.show({
            controller: editActivityDialogCtrl,
            templateUrl: './dialogs/editActivityDialog.html',
            locals: {
                activity: activity
            },
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        }).finally(function() {
            loadActivities($scope, $http);
        });
    };

    $scope.open_login_dialog = function(ev) {
        $mdDialog.show({
            controller: loginDialogCtrl,
            templateUrl: './dialogs/loginDialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        })
            .then(function() {
                //...
            });
    };

    $scope.delete_activity = function(activity) {
        var deleteRequest = {
            method : 'DELETE',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/activity/'+activity.id :
                heroku_address + '/activity/'+activity.id)
        };
        $http(deleteRequest).then(function() {
            loadActivities($scope, $http);
        });
    };

    $scope.confirm_delete_activity = function (activity, ev) {
        ev.stopPropagation();
        var confirm = $mdDialog.confirm()
            .title('Do you want to delete ' + activity.title + ' ?')
            .textContent('This will permanently remove the activity from our site.')
            .ariaLabel('Delete Activity')
            .targetEvent(ev)
            .ok('Yes, delete it.')
            .cancel('No, f*** go back.');
        $mdDialog.show(confirm).then(function() {
            $scope.delete_activity(activity);
        });
    };

    $scope.open_registration_dialog = function(ev) {
        $mdDialog.show({
            controller: registrationDialogCtrl,
            templateUrl: './dialogs/registrationDialog.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        }).then(function() {
            //...
        });
    };

    $scope.open_activity_details_dialog = function(activity, ev) {
        $mdDialog.show({
            controller: activityDetailsDialogCtrl,
            templateUrl: './dialogs/activityDetailsDialog.html',
            locals: {
                activity: activity
            },
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        }).then(function() {
            //...
        });
    };

    function addActivityDialogCtrl($scope, $mdDialog, $http) {
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.add = function(activity) {
            var postRequest = {
                method : 'POST',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/activity' :
                    heroku_address + '/activity'),
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

    function editActivityDialogCtrl($scope, $mdDialog, $http, activity) {
        $scope.activity = activity;
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.edit = function(activity) {
            var putRequest = {
                method : 'PUT',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/activity/' + activity.id :
                    heroku_address + '/activity/' + activity.id),
                data: {
                    title: $scope.activity.title,
                    text: $scope.activity.text,
                    tags: $scope.activity.tags
                }
            };
            $http(putRequest).then(function (response) {
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
            //Handle login
        };
        $scope.forgot_password = function () {
            //Handle forgot password
        }
    }

    function registrationDialogCtrl($scope, $mdDialog) {
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.registration = function(reg) {
            if (reg.password !== reg.passwordConfirm) {
                $scope.reg_form.reg_password_confirm.$setValidity("password", false);
                return;
            }
            var postRequest = {
                method : 'POST',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/registration' :
                    heroku_address + '/registration'),
                data: {
                    username: $scope.reg.name,
                    email: $scope.reg.email,
                    password: $scope.reg.password
                }
            };
            $http(postRequest).then(function (response) {
                // Work with response
            }).then(function () {
                $scope.cancel();
            });
        };
    }

    function activityDetailsDialogCtrl($scope, $mdDialog, activity) {
        $scope.activity = activity;
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }
});