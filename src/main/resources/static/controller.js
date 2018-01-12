var app = angular.module('IUA', ['ngMaterial','ngMessages']);

var heroku_address =  'https://iua.herokuapp.com';

app.config(function($mdThemingProvider) {
    // Preparation for costume colors.
    /*var calpoly_green = $mdThemingProvider.extendPalette('green', {
        '500': '#459926'
    });
    $mdThemingProvider.definePalette('calpoly_green', calpoly_green);*/
    $mdThemingProvider.theme('default')
        .primaryPalette('blue')
        .accentPalette('red');
});

app.filter('searchFieldActivities', function() {
    return function(items, search_text_field) {
        if (search_text_field === undefined) {
            return items;
        }
        var filtered = [];
        var letterMatch = new RegExp(search_text_field, 'i');
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            if (letterMatch.test(item.title)) {
                filtered.push(item);
            }
        }
        return filtered;
    };
});

app.filter('userActivities', function () {
    return function(items, search_type_useract, userID) {
        if (search_type_useract === false) {
            return items;
        }
        var filtered = [];
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            if (userID === item.author) {
                filtered.push(item);
            }
        }
        return filtered;
    }
});

app.filter('searchFieldUsers', function() {
    return function(items, search_text_field) {
        if (search_text_field === undefined) {
            return items;
        }
        if (items === undefined) {
            return items;
        }
        var filtered = [];
        var letterMatch = new RegExp(search_text_field, 'i');
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            if (letterMatch.test(item.name)) {
                filtered.push(item);
            }
        }
        return filtered;
    };
});

app.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

function loadActivities ($scope, $http){
    $http({
        method : 'GET',
        url: (window.location.hostname === 'localhost' ?
            'http://localhost:8080/activity' :
            heroku_address + '/activity')
    }).then(function (response) {
        $scope.activities = response.data;
        angular.forEach($scope.activities, function(value, key) {
            getUserData($scope, $http, value.author).then(function(data){
                value.authorName = data.name;
                value.authorPictureURL = window.location.href + 'user/' + value.author + '/picture';
                value.picture_url = window.location.href + 'activity/' + value.id + '/picture';
                value.dueDateRaw = value.dueDate;
                value.dueDate = moment(new Date(value.dueDate)).format('Do MMM YYYY');
            });
        })
    });
}

function getUserData ($scope, $http, userID) {
    var getRequest = {
        method : 'GET',
        url: (window.location.hostname === 'localhost' ?
            'http://localhost:8080/user/' + userID :
            heroku_address + '/user/' + userID)
    };
    return $http(getRequest).then(function (response) {
        return response.data;
    });
}

function getUsers ($scope, $http) {
    $http({
        method: 'GET',
        url: (window.location.hostname === 'localhost' ?
            'http://localhost:8080/user' :
            heroku_address + '/user')
    }).then(function (response) {
        $scope.users = response.data;
        angular.forEach($scope.users, function(value, key) {
            value.picture_url = window.location.href + 'user/' + value.id + '/picture';
        })
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
    if (window.location.hostname !== 'localhost' && location.protocol !== 'https:')
    {
        alert("This site only works in https:. Click ok to get redirected to https:");
        location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
    }
    // Later used for localization
    // console.log(navigator.language);

    var userid = getCookie("userid");
    var usertoken = getCookie("usertoken");
    var username = getCookie("username");
    var useremail = getCookie("useremail");

    if (userid !== "" && usertoken !== "" && username !== "" && useremail !== "") {
        $scope.current_user = {
            id: parseInt(userid),
            key: usertoken,
            name: username,
            email: useremail,
            picture_url: window.location.href + "user/" + userid + "/picture"
        };
        $scope.loginButtonHide = true;
    } else {
        $scope.loginButtonHide = false;
        $scope.current_user = null;
    }

    $scope.search_type_act = true;
    $scope.search_type_user = false;
    $scope.search_type_useract = false;

    loadActivities($scope, $http);

    var refreshInterval = setInterval(function check_for_new_activities () {
        $http({
            method : 'GET',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/activity' :
                heroku_address + '/activity')
        }).then(function (response) {
            if (response.data.length > $scope.activities.length) {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('New activities are available.')
                        .position('bottom right')
                        .action('Refresh')
                        .hideDelay(9000)
                ).then(function (response){
                    if (response === 'ok') {
                        loadActivities($scope, $http);
                    }
                });
            }
        }).catch(function (reason) {
            clearInterval(refreshInterval);
            $mdToast.show(
                $mdToast.simple()
                    .textContent('No connection to server possible.')
                    .position('bottom right')
                    .action('Refresh page')
                    .hideDelay(0)
            ).then(function (response) {
                if (response === 'ok') {
                    location.reload();
                }
            })
        });
    }, 90000);

    $scope.refresh_button = function () {
        $http({
            method : 'GET',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/activity' :
                heroku_address + '/activity')
        }).then(function (response) {
            $scope.activities = response.data;
            angular.forEach($scope.activities, function(value, key) {
                getUserData($scope, $http, value.author).then(function(data){
                    value.authorName = data.name;
                    value.authorPictureURL = window.location.href + 'user/' + value.author + '/picture';
                    value.picture_url = window.location.href + 'activity/' + value.id + '/picture';
                    value.dueDateRaw = value.dueDate;
                    value.dueDate = moment(new Date(value.dueDate)).format('Do MMM YYYY');
                });
            });
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Refreshed activities.')
                    .position('bottom right')
                    .hideDelay(3000)
            );
        }).catch(function (reason) {
            clearInterval(refreshInterval);
            $mdToast.show(
                $mdToast.simple()
                    .textContent('No connection to server possible.')
                    .position('bottom right')
                    .action('Refresh page')
                    .hideDelay(0)
            ).then(function (response) {
                if (response === 'ok') {
                    location.reload();
                }
            });
        });
    };

    $scope.toggleLeftSidebar = function() {
        $mdSidenav('left_Sidebar').toggle();
    };

    $scope.toggleRightSidebar = function(userID) {
        $scope.profile = {name: "ERROR", userID: userID};
        getUserData($scope, $http, userID).then(function(data){
            $scope.profile.name = data.name;
            $scope.profile.picture_url = window.location.href + 'user/' + userID + '/picture';
        });
        if ($scope.current_user !== null) {
            $scope.isSignedInUser = userID === $scope.current_user.id;
        } else {
            $scope.isSignedInUser = false;
        }
        $mdSidenav('right_Sidebar').toggle();
    };

    $scope.search_form_submit = function() {
        if ($scope.search_text_field === undefined) {
            return;
        }
        if ($scope.search_text_field.value !== "") {
            $scope.search_text_field = '';
        }
    };

    $scope.openUserMenu = function($mdMenu, ev) {
        $mdMenu.open(ev);
    };

    $scope.openFilterMenu = function ($mdMenu, ev) {
        $mdMenu.open(ev);
    };

    $scope.checkUser = function(activity) {
        if ($scope.current_user === null) {
            return false;
        }
        return $scope.current_user.id === activity.author;
    };

    $scope.logout = function() {
        setCookie("userid", "", -1);
        setCookie("usertoken", "", -1);
        setCookie("username", "", -1);
        setCookie("useremail", "", -1);
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

    $scope.go_to_imprint = function() {
        window.location.href = window.location + 'imprint.html';
    };

    $scope.open_support_dialog = function(current_user, ev) {
        $mdDialog.show({
            controller: supportDialogCtrl,
            templateUrl: './dialogs/supportDialog.html',
            parent: angular.element(document.body),
            locals: {
                current_user: current_user
            },
            targetEvent: ev,
            clickOutsideToClose:true
        });
    };

    $scope.open_message_dialog = function(current_user, ev) {
        $mdDialog.show({
            controller: contactUserDialogCtrl,
            templateUrl: './dialogs/contactUserDialog.html',
            parent: angular.element(document.body),
            locals: {
                current_user: current_user
            },
            targetEvent: ev,
            clickOutsideToClose:true
        });
    };

    $scope.open_add_activity_dialog = function(current_user, ev) {
        $mdDialog.show({
            controller: addActivityDialogCtrl,
            templateUrl: './dialogs/addActivityDialog.html',
            parent: angular.element(document.body),
            locals: {
                current_user: current_user
            },
            targetEvent: ev,
            clickOutsideToClose:true
        }).finally(function() {
            loadActivities($scope, $http);
        });
    };

    $scope.open_edit_activity_dialog = function(activity, current_user, ev) {
        ev.stopPropagation();
        $mdDialog.show({
            controller: editActivityDialogCtrl,
            templateUrl: './dialogs/editActivityDialog.html',
            locals: {
                activity: activity,
                current_user: current_user
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
            $scope.current_user = result.value;
            var length = (result.boolean !== undefined) ? 30 : 1;
            setCookie("userid", $scope.current_user.id, length);
            setCookie("usertoken", $scope.current_user.key, length);
            setCookie("useremail", $scope.current_user.email, length);
        }).finally(function() {
            if ($scope.current_user !== null) {
                getUserData($scope, $http, $scope.current_user.id).then(function(data) {
                    $scope.current_user.name = data.name;
                    setCookie("username", $scope.current_user.name, 30);
                });
                $scope.current_user.picture_url = window.location.href + "user/" + $scope.current_user.id + "/picture";
                $scope.loginButtonHide = true;
            }
        });
    };

    $scope.delete_activity = function(activity) {
        var deleteRequest = {
            method : 'DELETE',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/activity/'+activity.id + '?user=' + $scope.current_user.id + '&token=' + $scope.current_user.key :
                heroku_address + '/activity/'+activity.id + '?user=' + $scope.current_user.id + '&token=' + $scope.current_user.key)
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
                activity: activity,
                toggleUserProfile: function (userID) {
                    $scope.toggleRightSidebar(userID);
                }
            },
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        }).then(function() {
            //...
        });
    };

    $scope.open_edit_profile_dialog = function(current_user, ev) {
        $mdDialog.show({
            controller: editProfileDialogCtrl,
            templateUrl: './dialogs/editProfileDialog.html',
            locals: {
                current_user: current_user
            },
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose:true
        });
    };

    function addActivityDialogCtrl($scope, $mdDialog, $mdConstant, $http, current_user) {
        $scope.current_user = current_user;
        $scope.activity = {title: "", text: "", tags: [], pic: undefined};
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.SPACE];
        $scope.upload_in_progress = true;
        $scope.upload_finished = true;

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.add_pic = function() {
            if ($scope.activity_picture_upload === undefined) {
                return;
            }
            var data = new FormData();
            data.append('file', $scope.activity_picture_upload);
            $scope.activity.pic = data;
            $scope.upload_finished = false;
        };
        $scope.upload_pic = function(activity_id) {
            if ($scope.activity.pic === undefined) {
                return;
            }
            var url = "/activity/" + activity_id + "/picture?user=" + current_user.id + "&token=" + current_user.key;
            var config = {
                transformRequest: angular.identity,
                transformResponse: angular.identity,
                headers : {
                    'Content-Type': undefined
                }};
            $scope.upload_in_progress = false;
            $http.post(url, $scope.activity.pic, config).then(function () {
                $scope.upload_in_progress = true;
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Activity picture uploaded.')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            });
        };
        $scope.add = function(activity) {
            if ($scope.activity.dueDate === undefined) {
                return;
            }
            $scope.activity.dueDate = moment($scope.activity.dueDate).add(1, 'hour'); //Fix timezone
            var postRequest = {
                method : 'POST',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/activity?user=' + current_user.id + "&token=" + current_user.key :
                    heroku_address + '/activity?user=' + current_user.id + "&token=" + current_user.key),
                data: {
                    title: $scope.activity.title,
                    text: $scope.activity.text,
                    tags: $scope.activity.tags,
                    dueDate: $scope.activity.dueDate,
                    capacity: $scope.activity.capacity
                }
            };
            $http(postRequest).then(function (response) {
                $scope.activity.id = response.data.id;
                $scope.upload_pic($scope.activity.id);
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

    function editActivityDialogCtrl($scope, $mdDialog, $mdConstant, $mdToast, $http, activity, current_user) {
        $scope.activity = activity;
        $scope.activity.pic = undefined;
        $scope.activity.dueDateRaw = new Date($scope.activity.dueDateRaw);
        $scope.current_user = current_user;
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.SPACE];
        $scope.upload_in_progress = true;
        $scope.upload_finished = true;

        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.add_pic = function() {
            if ($scope.activity_picture_upload === undefined) {
                return;
            }
            var data = new FormData();
            data.append('file', $scope.activity_picture_upload);
            $scope.activity.pic = data;
            $scope.upload_finished = false;
        };
        $scope.upload_pic = function(activity_id) {
            if ($scope.activity.pic === undefined) {
                return;
            }
            var url = "/activity/" + activity_id + "/picture?user=" + current_user.id + "&token=" + current_user.key;
            var config = {
                transformRequest: angular.identity,
                transformResponse: angular.identity,
                headers : {
                    'Content-Type': undefined
                }};
            $scope.upload_in_progress = false;
            $http.post(url, $scope.activity.pic, config).then(function () {
                $scope.upload_in_progress = true;
            });
        };
        $scope.edit = function(activity) {
            if ($scope.activity.dueDate === undefined) {
                return;
            }
            $scope.activity.dueDateRaw = moment($scope.activity.dueDateRaw).add(1, 'hour'); //Fix timezone
            var putRequest = {
                method : 'PUT',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/activity/' + activity.id + '?user=' + current_user.id + '&token=' + current_user.key:
                    heroku_address + '/activity/' + activity.id + '?user=' + current_user.id + '&token=' + current_user.key),
                data: {
                    title: $scope.activity.title,
                    text: $scope.activity.text,
                    tags: $scope.activity.tags,
                    dueDate: $scope.activity.dueDateRaw,
                    capacity: $scope.activity.capacity
                }
            };
            $http(putRequest).then(function (response) {
                $scope.upload_pic($scope.activity.id);
            }).then(function () {
                $scope.cancel();
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Activity ' + $scope.activity.title + ' edited. Refresh the page to see the change.')
                        .position('bottom right')
                        .action('Refresh')
                        .hideDelay(0)
                ).then(function (response){
                    if (response === 'ok') {
                        location.reload();
                    }
                });
            });
        };
    }

    function loginDialogCtrl($scope, $mdDialog) {
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.hide = function(value, boolean) {
            var result = {value: value,
            boolean: boolean};
            $mdDialog.hide(result);
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
                    $scope.current_user.email = login.email;
                    $scope.hide($scope.current_user, login.staySignedIn)
                } else {
                    $scope.cancel();
                }
            }).catch(function (error) {
                var error_data = error.data;
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.login.UserNotValidatedException") {
                    if (error_data.message !== "No message available") {
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
        $scope.forgot_password = function (login) {
            $scope.login_form.login_email.$setValidity('emailNeededForReset', true);
            $scope.login_form.login_email.$setValidity('required', true);
            if (login.email === undefined) {
                $scope.login_form.login_email.$setValidity('emailNeededForReset', false);
                return;
            }
            var getRequest = {
                method : 'GET',
                url: (window.location.hostname === 'localhost' ?
                    'http://localhost:8080/register/request_reset?email=' + login.email :
                    heroku_address + '/register/request_reset?email=' + login.email)
            };
            $http(getRequest).then(function (response) {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('An e-mail with a password reset link was send to your address.')
                        .position('bottom right')
                        .hideDelay(3000)
                );
            }).catch(function (error) {
                var error_data = error.data;
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.login.UserNotValidatedException") {
                    if (error_data.message !== "No message available") {
                        $scope.login.timeTillUnlock = error_data.message;
                        $scope.login_form.login_email.$setValidity('accNotConfirmedLocked', false);
                    } else {
                        $scope.login_form.login_email.$setValidity('accNotConfirmedWithResend', false);
                    }
                } else {
                    $scope.login_form.login_email.$setValidity('accNotConfirmedLocked', true);
                    $scope.login_form.login_email.$setValidity('accNotConfirmedWithResend', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.login.UserNotFoundException") {
                    $scope.login_form.login_email.$setValidity('emailNotExists', false);
                } else {
                    $scope.login_form.login_email.$setValidity('emailNotExists', true);
                }
                if (error_data.exception.toString() === "edu.hm.cs.iua.exceptions.registration.EmailTransmissionFailed") {
                    $scope.login_form.login_email.$setValidity('sendEmailFailed', false)
                } else {
                    $scope.login_form.login_email.$setValidity('sendEmailFailed', true)
                }
            });
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

    function activityDetailsDialogCtrl($scope, $mdDialog, activity, toggleUserProfile) {
        $scope.activity = activity;
        $scope.toggleRightSidebar = function () {
            toggleUserProfile(activity.author);
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
    }

    function supportDialogCtrl($scope, $mdDialog, current_user) {
        if (current_user !== null) {
            $scope.support = {email: ""};
            $scope.support.email = current_user.email;
        }
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.send_message = function () {
            //...
        };
    }

    function contactUserDialogCtrl($scope, $mdDialog, current_user) {
        $scope.current_user = current_user;
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.send_message = function () {
            //...
        };
    }

    function editProfileDialogCtrl($scope, $mdDialog, current_user) {
        $scope.profile = current_user;
        $scope.upload_in_progress = true;
        $scope.upload_finished = true;
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
        $scope.edit = function() {
            //...
        };
        $scope.edit_pic = function() {
            if ($scope.profile_picture_upload === undefined) {
                return;
            }
            var url = "/user/" + current_user.id + "/picture?user=" + current_user.id + "&token=" + current_user.key;
            var data = new FormData();
            data.append('file', $scope.profile_picture_upload);
            var config = {
                transformRequest: angular.identity,
                transformResponse: angular.identity,
                headers : {
                    'Content-Type': undefined
                }};
            $scope.upload_in_progress = false;
            $http.post(url, data, config).then(function () {
                $scope.upload_in_progress = true;
                $scope.upload_finished = false;
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Profile picture changed. Refresh the page to see the change.')
                        .position('bottom right')
                        .action('Refresh')
                        .hideDelay(0)
                ).then(function (response){
                    if (response === 'ok') {
                        location.reload();
                    }
                });
            });
        };
    }
});

app.controller('UserMenuCtrl', function($scope) {
    $scope.search_type_useract = $scope.$parent.search_type_useract;
    $scope.search_type_useract_change = function () {
        $scope.$parent.search_type_useract = $scope.search_type_useract;
    };
});

app.controller('FilterMenuCtrl', function($scope, $http) {
    $scope.search_type_act = $scope.$parent.search_type_act;
    $scope.search_type_user = $scope.$parent.search_type_user;
    $scope.search_type_act_change = function () {
        $scope.$parent.search_type_user = !$scope.search_type_act;
        $scope.$parent.search_type_act = $scope.search_type_act;
        $scope.search_type_user = !$scope.search_type_act;
        if ($scope.search_type_user) {
            getUsers($scope.$parent, $http);
        }
    };

    $scope.search_type_user_change = function () {
        $scope.$parent.search_type_act = !$scope.search_type_user;
        $scope.$parent.search_type_user = $scope.search_type_user;
        $scope.search_type_act = !$scope.search_type_user;
        if ($scope.search_type_user) {
            getUsers($scope.$parent, $http);
        }
    };
});