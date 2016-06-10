
var app = angular.module("app",[]);

app.controller("CategoryController",CategoryController);

function CategoryController($scope, $http){
	$scope.parent = null;
	$scope.subcategories = null;
	$scope.pages = null;

	$scope.isLoadingGoback = false;

	$scope.load = function(id){
		$http.post("http://localhost:1025/load?id="+id)
		.then(function(response){
			$scope.parent = response.data.category;
			$scope.subcategories =  _(response.data.children)
				.filter(function(category){
					var lastSlash = category.url.lastIndexOf("/");
					var keyPart = category.url.substring(lastSlash+1);
					return keyPart.toLowerCase().startsWith("category:") && $scope.parent.hash != category.hash;
				});

			$scope.pages =  _(response.data.children)
				.filter(function(category){
					var lastSlash = category.url.lastIndexOf("/");
					var keyPart = category.url.substring(lastSlash+1);
					return !keyPart.toLowerCase().startsWith("category:") && $scope.parent.hash != category.hash;
				});		
			$scope.isLoadingGoback = false;	
			console.log("Hash of parent: " + $scope.parent.hash);
		},function(error){
			alert(error);
		})
	}

	$scope.alertStats = function(){
		var message = "* 1M+ links\n* 650K+ pages and subcategories";
		alert("Stats of Alt Category: \n" + message);
	};

	$scope.load(1);//People
}