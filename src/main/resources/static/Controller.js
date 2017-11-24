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
        $dialog.dialog(angular.extend(registrationDialogOptions, {})).open().then(function () {
            loadActivities($scope, $http);
        })
    };

    var loginDialogOptions = {
        controller: 'LoginCtrl',
        templateUrl: './login.html'
    };

    $scope.login = function() {
        $dialog.dialog(angular.extend(loginDialogOptions, {})).open().then(function () {
            loadActivities($scope, $http);
        })
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

app.controller('RegistrationCtrl', function($scope, $http, dialog) {
    $scope.save = function (User) {
        if ($scope.user.password !== $scope.user.passwordControl) {
            alert("Passwords don't match!");
        } else {
            alert("Works!");
        }
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
});

app.controller('LoginCtrl', function($scope, $http, dialog) {
    $scope.login = function (User) {
        alert("Works!");
    };

    $scope.close = function () {
        dialog.close(undefined);
    };
});

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
