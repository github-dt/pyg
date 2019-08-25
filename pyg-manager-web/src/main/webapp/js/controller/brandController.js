/** 品牌控制器层 */
app.controller('brandController', function ($scope, $controller, baseService) {

    // 指定继承baseController
    /**
     * $controller也是AngularJS提供的一个服务，可以实现伪继承，实际上就是与baseController共享$scope。
     */
    $controller("baseController", {$scope : $scope});


    // 定义查询品牌的方法
    $scope.findAll = function(){
        // 发送异步请求
        baseService.sendGet("/brand/findAll").then(function(response){
            // 获取响应数据 List<Brand> [{},{}]
            $scope.dataList = response.data;
        });
    };

    // 初始化封装查询条件的json对象
    $scope.searchEntity = {};

    // 定义分页查询方法
    $scope.search = function(page, rows){
        /** 发送异步请求 */
        baseService.findByPage("/brand/findByPage", page,
            rows, $scope.searchEntity)
            .then(function(response){
                // response.data: List<Brand> [{},{}] 与 总记录数
                // {"rows" : [{},{}], "total" : 100}
                // 获取查询的数据
                $scope.dataList = response.data.rows;
                // 更新分页组件中的总记录数
                $scope.paginationConf.totalItems = response.data.total;
            });
    };


    // 定义添加或修改品牌的方法
    $scope.saveOrUpdate = function () {
        //alert(JSON.stringify($scope.entity));

        var url = "save"; // 添加
        if ($scope.entity.id){
            url = "update"; // 修改
        }
        // 发送post请求
        baseService.sendPost("/brand/" + url, $scope.entity)
            .then(function(response){
                // true,false
                if (response.data){
                    // 添加成功，重新加载数据
                    $scope.reload();
                }else{
                    alert("添加失败！");
                }
            });
    };

    // 为修改按钮绑定点击事件
    $scope.show = function(entity){
        // 把entity转化成json字符串，把json字符串转化成新的json对象
        $scope.entity =  JSON.parse(JSON.stringify(entity));
    };


    // 删除方法
    $scope.delete = function(){
        // 判断
        if ($scope.ids.length > 0){
            // 删除
            baseService.deleteById("/brand/delete", $scope.ids)
                .then(function(response){
                    if (response.data){ // 删除成功
                        // 清空数组
                        $scope.ids = [];
                        // 重新加载数据
                        $scope.reload();
                    }else {
                        alert("删除失败！");
                    }
                });

        }else{
            alert("请选择要删除的品牌！");
        }
    };
});