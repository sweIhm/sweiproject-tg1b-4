var app = angular.module('ActivityMeterApp', ['ui.bootstrap']);

function loadActivities ($scope, $http){
    $http({
        method : 'GET',
        /*
         url: (window.location.hostname === 'localhost' ?
         'http://localhost:8080/activity' :
         'https://activityexample.herokuapp.com/activity')
         */
        url: 'activity'

    }).then(function (response) {
        $scope.activities = response.data;
    });
}

app.controller('ActivityCtrl', function ($scope, $http, $dialog) {

    loadActivities($scope, $http);

    var registrationDialogOptions = {
        controller: 'RegistrationCtrl',
        templateUrl: './registration.html'
    };

    $scope.registration = function() {
        var dialog = document.getElementById('reg_dialog');
        dialog.showModal();
    };

    $scope.reg_save = function (User) {
        if ($scope.user.password !== $scope.user.passwordControl) {
            alert("Passwords don't match!");
            document.getElementById("passwordControl").focus();
            return;
        }
        alert("Works!");
        var postRequest = {
            method : 'POST',
            url: 'user',
            data: {
                username: $scope.user.username,
                email: $scope.user.email,
                password: $scope.user.password
            }
        };
    };

    $scope.reg_close = function () {
        var dialog = document.getElementById('reg_dialog');
        dialog.close();
    };

    $scope.login = function() {
        var dialog = document.getElementById('login_dialog');
        dialog.showModal();
        dialog.addEventListener('click', function (event) {
            var rect = dialog.getBoundingClientRect();
            var isInDialog = (rect.top <= event.clientY && event.clientY <= rect.top + rect.height
                && rect.left <= event.clientX && event.clientX <= rect.left + rect.width);
            if (!isInDialog) {
                dialog.close();
            }
        })
    };

    $scope.login_login = function() {
        alert("Login Works!");
        login_close();
    };

    $scope.login_close = function() {
        var dialog = document.getElementById('login_dialog');
        dialog.close();
    };

    var addDialogOptions = {
        controller: 'AddActivityCtrl',
        templateUrl: './activityAdd.html'
    };

    $scope.add = function(activity){
        $dialog.dialog(angular.extend(addDialogOptions, {})).open().then(function (){
            loadActivities($scope, $http);
        }) ;
    };

    var editDialogOptions = {
        controller: 'EditActivityCtrl',
        templateUrl: './activityEdit.html',
    };
    $scope.edit = function(activity){
        var activityToEdit = activity;
        $dialog.dialog(angular.extend(editDialogOptions, {resolve: {activity: angular.copy(activityToEdit)}})).open().then(function (){
            loadActivities($scope, $http);
        }) ;
    };

    $scope.delete = function(activity) {
        var deleteRequest = {
            method : 'DELETE',
            url: 'activity/' + activity.id
        };

        $http(deleteRequest).then(function() {
            loadActivities($scope, $http);
        });
        //todo handle error
    };
});

/*app.controller('RegistrationCtrl', function($scope, $http, dialog) {
    $scope.save = function (User) {
        if ($scope.user.password !== $scope.user.passwordControl) {
            alert("Passwords don't match!");
            document.getElementById("passwordControl").focus();
            return;
        }
        alert("Works!");
        var postRequest = {
            method : 'POST',
            url: 'user',
            data: {
                username: $scope.user.username,
                email: $scope.user.email,
                password: $scope.user.password
            }
        };
    };

    $scope.close = function () {
        dialog.close(undefined);
    };
});*/

/*app.controller('LoginCtrl', function($scope, $http, dialog) {
    $scope.login = function (User) {
        alert("Login Works!");
    };

    $scope.close = function () {
        var dialog = document.getElementById('dialog');
        dialog.close();
    };
});*/

app.controller('AddActivityCtrl', function($scope, $http, dialog){

    $scope.save = function(Activity) {
        var postRequest = {
            method : 'POST',
            url: 'activity' ,
            data: {
                text: $scope.activity.text,
                tags: $scope.activity.tags,
                title: $scope.activity.title
            }
        };

        $http(postRequest).then(function (response) {
            $scope.activities = response.data;
        }).then(function () {
            $scope.close();
        });
    };

    $scope.close = function(){
        dialog.close(undefined);
    };
});
app.controller('EditActivityCtrl', function ($scope, $http, activity, dialog) {

    $scope.activity = activity;
    $scope.save = function($activity) {
        var putRequest = {
            method : 'PUT',
            url: 'activity/' + $scope.activity.id,
            data: {
                text: $scope.activity.text,
                tags: $scope.activity.tags,
                title: $scope.activity.title
            }
        }

        $http(putRequest).then(function (response) {
            $scope.activities = response.data;
        }).then(function () {
            //todo handle error
            $scope.close();
        });
    };

    $scope.close = function(){
        loadActivities($scope, $http);
        dialog.close();
    };
});
