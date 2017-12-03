var app = angular.module('IUA_new', ['ngMaterial','ngMessages']);

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

    $scope.open_registration_code_dialog = function (user, ev) {
        var confirm = $mdDialog.prompt()
            .title('Please enter the code we send to your e-mail:')
            .placeholder('Confirmation code:')
            .ariaLabel('Confirmation code:')
            .targetEvent(ev)
            .required(true)
            .ok('Enter')
            .cancel('Cancel');
        $mdDialog.show(confirm).then(function(result) {
            //send code to server and request confirmation
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
        var response;

        $scope.cancel = function() {
            $mdDialog.cancel();
        };

        $scope.open_registration_code_dialog = function (user, ev) {
            var confirm = $mdDialog.prompt()
                .title('Please enter the code we send to your e-mail:')
                .placeholder('Confirmation code:')
                .ariaLabel('Confirmation code:')
                .targetEvent(ev)
                .required(true)
                .ok('Enter')
                .cancel('Cancel');
            $mdDialog.show(confirm).then(function(result) {
                //send code to server and request confirmation
            });
        };

        $scope.registration = function(reg) {
            if (reg.password !== reg.passwordConfirm) {
                $scope.reg_form.reg_password_confirm.$setValidity('password', false);
                return;
            } else {
                $scope.reg_form.reg_password_confirm.$setValidity('password', true);
            }
            var postRequest = {
                method : 'POST',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/register' :
                    heroku_address + '/register'),
                data: {
                    name: $scope.reg.name,
                    email: $scope.reg.email,
                    password: $scope.reg.password
                }
            };
            $http(postRequest).then(function (response) {
                $scope.response = response;
            }).then(function () {
                $scope.cancel();
                $scope.open_registration_code_dialog(response);
            }).catch(function (error) {
                var error_data = error.data;
                alert(error_data.exception);
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.InvalidDataException") {
                    if (error_data.message === "Email invalid.") {
                        $scope.reg_form.reg_email.$setValidity('noHMorCalEmail', false);
                    } else {
                        $scope.reg_form.reg_email.$setValidity('noHMorCalEmail', true);
                    }
                } else {
                    $scope.reg_form.reg_email.$setValidity('noHMorCalEmail', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.EmailAlreadyTakenException") {
                    $scope.reg_form.reg_email.$setValidity('emailAlreadyTaken', false);
                } else {
                    $scope.reg_form.reg_email.$setValidity('emailAlreadyTaken', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.UsernameAlreadyTakenException") {
                    $scope.reg_form.reg_name.$setValidity('nameAlreadyTaken', false);
                } else {
                    $scope.reg_form.reg_name.$setValidity('nameAlreadyTaken', true);
                }
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