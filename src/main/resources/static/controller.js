var app = angular.module('IUA_new', ['ngMaterial','ngMessages']);

var heroku_address =  'https://iua-experimental.herokuapp.com';

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

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

app.controller('IUACtrl', function($scope, $http, $mdSidenav, $mdDialog, $mdToast) {

    // Check if user call's site from http and redirect to https if true.
    if (location.protocol !== 'https:')
    {
        alert("This site only works in https:. Click ok to get redirected to https:");
        location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
    }

    var userid = getCookie("userid");
    var usertoken = getCookie("usertoken");
    var username = getCookie("username");

    if (userid !== "" && usertoken !== "" && username !== "") {
        $scope.current_user = {
            id: userid,
            token: usertoken,
            name: username
        };
        $scope.loginButtonHide = true;
    } else {
        $scope.loginButtonHide = false;
        $scope.current_user = null;
    }

    loadActivities($scope, $http);

    $scope.toggleLeftSidebar = function() {
        $mdSidenav('left_Sidebar').toggle();
    };

    $scope.search_form_submit = function() {
        if ($scope.search_text_field.value !== "") {
            $scope.search_text_field = '';
        }
    };

    $scope.openUserMenu = function($mdMenu, ev) {
        $mdMenu.open(ev);
    };

    $scope.logout = function() {
        setCookie("userid", "", -1);
        setCookie("usertoken", "", -1);
        setCookie("username", "", -1);
        $scope.loginButtonHide = false;
        $scope.current_user = null;
        $mdToast.show(
            $mdToast.simple()
                .textContent('You signed out successfully.')
                .position('bottom right')
                .hideDelay(3000)
        );
    };

    $scope.add_test_user = function () {
        var getRequest = {
            method : 'GET',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/test' :
                heroku_address + '/test')
        };
        $http(getRequest).then(function () {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Test user activated.')
                    .position('bottom right')
                    .hideDelay(3000)
            );
        });
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
        }).then(function(result) {
            $scope.current_user = result;
            setCookie("userid", $scope.current_user.id, 1);
            setCookie("usertoken", $scope.current_user.token, 1);
        }).finally(function() {
            if ($scope.current_user !== null) {
                var getRequest = {
                    method : 'GET',
                    url: (window.location.hostname === 'localhost' ?
                        'http://localhost:8080/user/' + $scope.current_user.id :
                        heroku_address + '/user/' + $scope.current_user.id)
                };
                $http(getRequest).then(function (response) {
                    $scope.current_user.name = response.data.name;
                    setCookie("username", $scope.current_user.name, 1);
                });
                $scope.loginButtonHide = true;
            }
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
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Activity ' + activity.title + ' deleted.')
                    .position('bottom right')
                    .hideDelay(3000)
            );
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
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Activity ' + $scope.activity.title + ' added.')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            });
        };
    }

    function editActivityDialogCtrl($scope, $mdDialog, $mdToast, $http, activity) {
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
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Activity ' + $scope.activity.title + ' edited.')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            });
        };
    }

    function loginDialogCtrl($scope, $mdDialog) {
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.hide = function(value) {
            $mdDialog.hide(value);
        };
        $scope.current_user = null;
        $scope.login = function(login) {
            var getRequest = {
                method : 'GET',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/login?email=' + login.email + '&password=' + login.password :
                    heroku_address + '/login?email=' + login.email + '&password=' + login.password)
            };
            $http(getRequest).then(function (response) {
                $scope.current_user = response.data;
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('You signed in successfully')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            }).then(function () {
                if ($scope.current_user !== null) {
                    $scope.hide($scope.current_user)
                } else {
                    $scope.cancel();
                }
            }).catch(function (error) {
                var error_data = error.data;
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.login.UserNotValidatedException") {
                    if (error_data.message !== "") {
                        $scope.login.timeTillUnlock = error_data.message;
                        $scope.login_form.login_email.$setValidity('accNotConfirmedLocked', false);
                    } else {
                        $scope.login_form.login_email.$setValidity('accNotConfirmedWithResend', false);
                    }
                } else {
                    $scope.login_form.login_email.$setValidity('accNotConfirmedLocked', true);
                    $scope.login_form.login_email.$setValidity('accNotConfirmedWithResend', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.login.InvalidPasswordException") {
                    $scope.login_form.login_password.$setValidity('wrongPassword', false);
                } else {
                    $scope.login_form.login_password.$setValidity('wrongPassword', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.login.UserNotFoundException") {
                    $scope.login_form.login_email.$setValidity('emailNotExists', false);
                } else {
                    $scope.login_form.login_email.$setValidity('emailNotExists', true);
                }
            });
        };
        $scope.forgot_password = function () {
            //Handle forgot password
        }
    }

    function registrationDialogCtrl($scope, $mdDialog, $mdToast) {
        $scope.registration_in_progress = true;
        $scope.cancel = function() {
            $mdDialog.cancel();
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
            $scope.registration_in_progress = false;
            $http(postRequest).then(function (response) {
                //...
            }).then(function () {
                $scope.registration_in_progress = true;
                $scope.cancel();
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('User ' + $scope.reg.name + ' created.')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            }).catch(function (error) {
                var error_data = error.data;
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.registration.InvalidDataException") {
                    if (error_data.message === "Email invalid.") {
                        $scope.reg_form.reg_email.$setValidity('noHMorCalEmail', false);
                    } else {
                        $scope.reg_form.reg_email.$setValidity('noHMorCalEmail', true);
                    }
                } else {
                    $scope.reg_form.reg_email.$setValidity('noHMorCalEmail', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.registration.EmailAlreadyTakenException") {
                    $scope.reg_form.reg_email.$setValidity('emailAlreadyTaken', false);
                } else {
                    $scope.reg_form.reg_email.$setValidity('emailAlreadyTaken', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.registration.UsernameAlreadyTakenException") {
                    $scope.reg_form.reg_name.$setValidity('nameAlreadyTaken', false);
                } else {
                    $scope.reg_form.reg_name.$setValidity('nameAlreadyTaken', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.registration.EmailTransmissionFailed") {
                    $scope.reg_form.reg_email.$setValidity('sendEmailFailed', false)
                } else {
                    $scope.reg_form.reg_email.$setValidity('sendEmailFailed', true)
                }
                $scope.registration_in_progress = true;
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