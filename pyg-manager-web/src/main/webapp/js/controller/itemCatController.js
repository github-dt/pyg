/** 定义控制器层 */
app.controller('itemCatController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询全部 */
    $scope.findAll = function(){
        baseService.sendGet("/itemCat/findAll").then(function(response){
            $scope.dataList = response.data;
        });
    };

    /** 定义搜索对象 */
    $scope.searchEntity = {};
    /** 分页查询 */
    $scope.search = function(page, rows){
        baseService.findByPage("/itemCat/findByPage", page, 
			rows, $scope.searchEntity)
            .then(function(response){
                $scope.dataList = response.data.rows;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){
        var url = "save";
        if ($scope.entity.id){
            url = "update";
        }
        /** 发送post请求 */
        baseService.sendPost("/itemCat/" + url, $scope.entity)
            .then(function(response){
                if (response.data){
                    /** 重新加载数据 */
                    $scope.reload();
                }else{
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function(entity){
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/itemCat/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }
    };

    /** 根据上级ID显示下级列表 */
    $scope.findItemCatByParentId = function(parentId){
        baseService.sendGet("/itemCat/findItemCatByParentId",
            "parentId=" + parentId).then(
            function(response){
                $scope.dataList = response.data;
            }
        );
    };

    /** 默认为1级 */
    $scope.grade = 1;
    /** 查询下级 */
    $scope.selectList = function(entity, grade){
        $scope.grade = grade;
        if(grade == 1){ //如果为1级
            $scope.itemCat_1 = null;
            $scope.itemCat_2 = null;
        }
        if(grade == 2){ //如果为2级
            $scope.itemCat_1 = entity;
            $scope.itemCat_2 = null;
        }
        if(grade == 3){ //如果为3级
            $scope.itemCat_2 = entity;
        }
        /** 查询此级下级列表 */
        $scope.findItemCatByParentId(entity.id);
    };


});