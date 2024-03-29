/** 控制器层 */
app.controller("indexController", function($scope, baseService){
    /** 定义获取当前登录用户名方法 */
    $scope.showLoginName = function(){
        baseService.sendGet("/login/username").then(
            function(response){
                $scope.loginName = response.data.loginName;
            }
        );
    };
});
