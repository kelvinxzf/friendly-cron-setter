angular.module('acs-commons-cron-setter-app', ['acsCoral', 'ACS.Commons.notifications'])
    .controller('MainCtrl', ['$scope', '$http', '$timeout', 'NotificationsService',
        function ($scope, $http, $timeout, NotificationsService) {

            $scope.app = {
                resource: '',
                running: false,
                refresh: ''
            };
            
            $scope.pid_select = $('#pid').get(0);
            
            $scope.cron_field = {};
            
            $scope.init = function() {
            	$http({
                    method: 'GET',
                    url: encodeURI($scope.app.resource + '.list.json')
                }).
                   	success(function (data, status, headers, config) {
                        var items = data || {};
                        items.forEach(function(value, index) {
                        	$scope.pid_select.items.add(value);
                          });
                     }).
                    error(function (data, status, headers, config) {
                        NotificationsService.add('error',
                            'ERROR', 'Unable to retrieve scheduler list');
                    });
            	
            	$scope.pid_select.on('change', function() {
            		$scope.getExpression();
            	    });
            	
            	$scope.cron_field = $('#selector').cron({
            		initial: "0 0/1 * * * ?",
            		customValues: {
            	        "2 minutes" : "0 0/2 * * * ?"
            	    },
            		onChange: function() {
                        $('#selector-val').text($(this).cron("value"));
                    }
                });
            };

            $scope.getExpression = function () {
                $http({
                    method: 'GET',
                    url: encodeURI($scope.app.resource + '.service.json?pid=' + $scope.pid_select.value)
                }).
                   	success(function (data, status, headers, config) {
                        var cron = data || { "expression": "null" };
                        $('.cron-selector').show();
                        if (!cron.expression || cron.expression == "null"){
                        	alert ("This scheduler cron hasn't been configured yet and you can configure it now.");
                        	$scope.cron_field.cron("value", "0 0/1 * * * ?");
                   		}
                        else {
                        	$scope.cron_field.cron("value", cron.expression);
                        }
                     }).
                    error(function (data, status, headers, config) {
                        NotificationsService.add('error',
                            'ERROR', 'Unable to retrieve cron expression');
                    });
            };
            
            $scope.updateExpression = function () {
            	var cron_form = {
                		pid:'',
                		expression:''
                };
            	cron_form.pid=$scope.pid_select.value;
            	cron_form.expression=$scope.cron_field.cron("value");
            	
                $http({
                    method: 'POST',
                    url: encodeURI($scope.app.resource + '.service.json'),
                    data: JSON.stringify(cron_form),
                    headers: {'Content-Type': 'application/json; charset=UTF-8'}
                }).
                   	success(function (data, status, headers, config) {
                   		NotificationsService.add('info',
                                'INFO', '"' + cron_form.pid + '" has been updated!');
                     }).
                    error(function (data, status, headers, config) {
                        NotificationsService.add('error',
                            'ERROR', 'Unable to update!');
                    });
            };
            
        }]);


