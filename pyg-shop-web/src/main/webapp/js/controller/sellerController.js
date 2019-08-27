/** 定义控制器层 */
app.controller('sellerController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});



    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        var url = "save";
        /** 发送post请求 */
        baseService.sendPost("/seller/" + url, $scope.seller)
            .then(function(response){
                if (response.data){
                    /** 跳转到商家登录页面 */
                    location.href = "/shoplogin.html";
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 定义搜索对象 */
    $scope.searchEntity = {};
    /** 分页查询 */
    $scope.search = function(page, rows){
        baseService.findByPage("/seller/findByPage", page,
            rows, $scope.searchEntity)
            .then(function(response){
                $scope.dataList = response.data.rows;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 显示修改 */
    $scope.show = function(entity){
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/seller/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }
    };

});