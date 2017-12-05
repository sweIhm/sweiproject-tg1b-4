var app = angular.module('IUA', ['ui.bootstrap']);

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

app.controller('IUACtrl', function ($scope, $http) {

    loadActivities($scope, $http);

    $scope.open_registration_dialog = function() {
        var dialog = document.getElementById('reg_dialog');
        dialog.showModal();
    };

    $scope.reg_save = function () {
        if (document.getElementById('reg_password').value !== document.getElementById('reg_passwordControl').value) {
            document.getElementById("reg_passwordControlError").innerHTML = "Passwords don't match!";
            document.getElementById("reg_passwordControl").focus();
            return;
        }
        alert("Registration form completed.");
        var postRequest = {
            method : 'POST',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/registration' :
                'https://iua.herokuapp.com/registration'),
            data: {
                username: document.getElementById('reg_username').value,
                email: document.getElementById('reg_email').value,
                password: document.getElementById('reg_password').value
            }
        };
        $http(postRequest).then(function (response) {
            // Work with response
        }).then(function () {
            $scope.reg_close();
        });
    };

    $scope.reg_close = function () {
        var dialog = document.getElementById('reg_dialog');
        document.getElementById('reg_form').reset();
        document.getElementById('reg_passwordControlError').innerHTML = "";
        dialog.close();
    };

    $scope.open_login_dialog = function() {
        var dialog = document.getElementById('login_dialog');
        dialog.showModal();
    };

    $scope.login = function() {
        alert("Login Works!");
        var postRequest = {
            method : 'POST',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/login' :
                'https://iua.herokuapp.com/login'),
            data: {
                email: document.getElementById('login_email').value,
                password: document.getElementById('login_password').value
            }
        };
        $http(postRequest).then(function (response) {
            // Work with response
        }).then(function () {
            $scope.login_close();
        });
    };

    $scope.login_close = function() {
        var dialog = document.getElementById('login_dialog');
        document.getElementById('login_form').reset();
        dialog.close();
    };

    $scope.open_add_dialog = function(){
        var dialog = document.getElementById('add_activity_dialog');
        dialog.showModal();
    };

    $scope.add_save = function() {
        var postRequest = {
            method : 'POST',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/activity' :
                'https://iua.herokuapp.com/activity'),
            data: {
                title: document.getElementById('add_title').value,
                text: document.getElementById('add_text').value,
                tags: document.getElementById('add_tags').value
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
        document.getElementById('add_form').reset();
        dialog.close();
    };

    var edit_activity_id;

    $scope.open_edit_dialog = function(activity){
        edit_activity_id = activity.id;
        var dialog = document.getElementById('edit_activity_dialog');
        document.getElementById('edit_title').value = activity.title;
        document.getElementById('edit_text').value = activity.text;
        document.getElementById('edit_tags').value = activity.tags;
        dialog.showModal();
    };

    $scope.edit_save = function() {
        var putRequest = {
            method : 'PUT',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/activity/'+edit_activity_id :
                'https://iua.herokuapp.com/activity/'+edit_activity_id),
            data: {
                title: document.getElementById('edit_title').value,
                text: document.getElementById('edit_text').value,
                tags: document.getElementById('edit_tags').value
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
        document.getElementById('edit_form').reset();
        dialog.close();
    };

    $scope.delete = function(activity) {
        var deleteRequest = {
            method : 'DELETE',
            url: (window.location.hostname === 'localhost' ?
                'http://localhost:8080/activity/'+activity.id :
                'https://iua.herokuapp.com/activity/'+activity.id)
        };
        $http(deleteRequest).then(function() {
            loadActivities($scope, $http);
        });
    };
});
