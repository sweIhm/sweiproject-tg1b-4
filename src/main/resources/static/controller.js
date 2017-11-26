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

app.controller('ActivityCtrl', function ($scope, $http) {

    loadActivities($scope, $http);

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
                $scope.login_close();
            }
        })
    };

    $scope.login_login = function() {
        alert("Login Works!");
        $scope.login_close();
    };

    $scope.login_close = function() {
        var dialog = document.getElementById('login_dialog');
        document.getElementById('emailLogin').value = "";
        document.getElementById('passwordLogin').value = "";
        dialog.close();
    };

    $scope.add = function(activity){
        var dialog = document.getElementById('add_activity_dialog');
        //document.getElementById('add_title').value = "";
        dialog.showModal();
        dialog.addEventListener('click', function (event) {
            var rect = dialog.getBoundingClientRect();
            var isInDialog = (rect.top <= event.clientY && event.clientY <= rect.top + rect.height
                && rect.left <= event.clientX && event.clientX <= rect.left + rect.width);
            if (!isInDialog) {
                $scope.add_close();
            }
        })
    };

    $scope.add_save = function(Activity) {
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
            loadActivities($scope, $http);
            $scope.add_close();
        });
    };

    $scope.add_close = function(){
        var dialog = document.getElementById('add_activity_dialog');
        document.getElementById('add_title').value = "";
        document.getElementById('add_text').value = "";
        document.getElementById('add_tags').value = "";
        dialog.close();
    };

    var edit_activity_id;

    $scope.edit = function(activity){
        edit_activity_id = activity.id;
        var dialog = document.getElementById('edit_activity_dialog');
        document.getElementById('edit_title').value = activity.title;
        document.getElementById('edit_text').value = activity.text;
        document.getElementById('edit_tags').value = activity.tags;
        dialog.showModal();
        dialog.addEventListener('click', function (event) {
            var rect = dialog.getBoundingClientRect();
            var isInDialog = (rect.top <= event.clientY && event.clientY <= rect.top + rect.height
                && rect.left <= event.clientX && event.clientX <= rect.left + rect.width);
            if (!isInDialog) {
                $scope.edit_close();
            }
        })
    };

    $scope.edit_save = function(activity) {
        var putRequest = {
            method : 'PUT',
            url: 'activity/' + edit_activity_id,
            data: {
                text: $scope.activity.text,
                tags: $scope.activity.tags,
                title: $scope.activity.title
            }
        };

        $http(putRequest).then(function (response) {
            $scope.activities = response.data;
        }).then(function () {
            loadActivities($scope, $http);
            $scope.edit_close();
        });
    };

    $scope.edit_close = function(){
        var dialog = document.getElementById('edit_activity_dialog');
        document.getElementById('edit_title').value = "";
        document.getElementById('edit_text').value = "";
        document.getElementById('edit_tags').value = "";
        dialog.close();
    };

    $scope.delete = function(activity) {
        var deleteRequest = {
            method : 'DELETE',
            url: 'activity/' + activity.id
        };

        $http(deleteRequest).then(function() {
            loadActivities($scope, $http);
        });
    };
});
