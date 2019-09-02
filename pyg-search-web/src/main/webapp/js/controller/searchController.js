/** 定义搜索控制器 */
app.controller('searchController',function($scope, baseService){
    /** 定义搜索参数对象 */
    $scope.searchParam = {};
    /** 定义搜索方法 */
    $scope.search = function(){
        baseService.sendPost("/Search", $scope.searchParam)
            .then(function(response){
                    /** 获取搜索结果 */
                    $scope.resultMap = response.data;
                }
            );
    };

    /** 定义搜索参数对象 */
    $scope.searchParam = {'keywords' : '', 'category' : '',
        'brand' : '', 'spec' : {}};
    /** 添加搜索选项方法 */
    $scope.addSearchItem = function(key, value){
        /** 判断是商品分类还是品牌 */
        if(key == 'category' || key == 'brand'){
            $scope.searchParam[key] = value;
        }else{
            /** 规格选项 */
            $scope.searchParam.spec[key] = value;
        }
        /** 执行搜索 */
        $scope.search();

    };

    /** 删除搜索选项方法 */
    $scope.removeSearchItem = function(key){
        /** 判断是商品分类还是品牌 */
        if(key == "category" ||  key == "brand"){
            $scope.searchParam[key] = "";
        }else{
            /** 删除规格选项 */
            delete $scope.searchParam.spec[key];
        }
        /** 执行搜索 */
        $scope.search();

    };

});
