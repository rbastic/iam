'use strict';

angular.module('dashboardApp').controller('EditPasswordController',
		EditPasswordController);

EditPasswordController.$inject = [ '$scope', '$state', '$uibModalInstance', 'Utils',
		'ResetPasswordService', 'user' ];

function EditPasswordController($scope, $state, $uibModalInstance, Utils,
		ResetPasswordService, user) {

	var editPasswordCtrl = this;

	editPasswordCtrl.passwordMinlength = 5;
	editPasswordCtrl.userToEdit = user;

	editPasswordCtrl.dismiss = dismiss;
	editPasswordCtrl.reset = reset;
	
	function reset() {
		console.log("reset form")
		editPasswordCtrl.enabled = true;
		editPasswordCtrl.user = {
				currentPassword: "",
				password: "",
				confirmPassword: ""
		};
		if ($scope.editPasswordForm) {
			$scope.editPasswordForm.$setPristine();
		}
	}

	editPasswordCtrl.reset();

	function dismiss() {
		$uibModalInstance.dismiss('Cancel');
	}

	editPasswordCtrl.message = "";

	editPasswordCtrl.submit = function() {
		ResetPasswordService.updatePassword(
				editPasswordCtrl.user.currentPassword,
				editPasswordCtrl.user.password).then(
				function(result) {
					console.info("Password changed");
					editPasswordCtrl.dismiss();
				},
				function(error) {
					console.error(error);
					$scope.operationResult = Utils
							.buildGenericErrorOperationResult(error.data);
				});
	};

};

var compareTo = function() {
	return {
		require : "ngModel",
		scope : {
			otherModelValue : "=compareTo"
		},
		link : function(scope, element, attributes, ngModel) {

			ngModel.$validators.compareTo = function(modelValue) {
				return modelValue == scope.otherModelValue;
			};

			scope.$watch("otherModelValue", function() {
				ngModel.$validate();
			});
		}
	};
};

angular.module('dashboardApp').directive("compareTo", compareTo);