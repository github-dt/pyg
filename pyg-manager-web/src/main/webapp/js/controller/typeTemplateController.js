/** 定义控制器层 */
app.controller('typeTemplateController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 定义搜索对象 */
    $scope.searchEntity = {};
    /** 分页查询 */
    $scope.search = function(page, rows){
        baseService.findByPage("/typeTemplate/findByPage", page, 
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
        baseService.sendPost("/typeTemplate/" + url, $scope.entity)
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

        /** 转换品牌列表 */
        $scope.entity.brandIds = JSON.parse(entity.brandIds);

        /** 转换规格列表 */
        $scope.entity.specIds = JSON.parse(entity.specIds);

        /** 转换扩展属性 */
        $scope.entity.customAttributeItems = JSON
            .parse(entity.customAttributeItems);

    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/typeTemplate/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }
    };

    /** 品牌列表 */
    $scope.findBrandList = function(){
        baseService.sendGet("/brand/selectBrandList")
            .then(function(response){
            // $scope.brandList = {data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};
            $scope.brandList = {data:response.data};
            });
    };

    /** 规格列表 */
    $scope.findSpecList = function(){
        baseService.sendGet("/specification/selectSpecList")
            .then(function(response){
                $scope.specList = {data:response.data};
            });
    };

    /** 新增扩展属性行 */
    $scope.addTableRow = function(){
        $scope.entity.customAttributeItems.push({});
    };

    /** 删除扩展属性行 */
    $scope.deleTableRow = function(index){
        $scope.entity.customAttributeItems.splice(index,1);
    };

    /** 更新品牌与规格选项的缓存数据 */
    $scope.updateRedis = function(){
        baseService.sendGet("/typeTemplate/updateRedis")
            .then(function(response){
                if (response.data){
                    alert("更新缓存成功！");
                }else{
                    alert("更新缓存失败！");
                }
            });
    };

});