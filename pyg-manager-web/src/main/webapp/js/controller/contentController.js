/** 控制器层 */
app.controller("contentController", function($scope, $controller, uploadService, baseService){

	/** 指定继承baseController */
	$controller('baseController',{$scope:$scope});

	/** 定义搜索对象 */
	$scope.searchEntity = {};	
	/** 分页查询信息 */
	$scope.search = function(page, rows){
		/** 调用服务层分页查询数据 */
		baseService.findByPage("/content/findByPage",page,
			rows,$scope.searchEntity)
			.then(function(response){
				$scope.dataList = response.data.rows;	
				/** 更新总记录数 */
				$scope.paginationConf.totalItems = response.data.total;
		});
	};

	// 定义状态码数组
	$scope.status = ['无效','有效'];

	/** 添加与修改 */
	$scope.saveOrUpdate = function(){
		/** 定义请求URL */
		var url = "save"; // 添加
		if ($scope.entity.id){
			url = "update"; // 修改
		}
		/** 调用服务层 */
		baseService.sendPost("/content/" + url, $scope.entity)
			.then(function(response){
				if (response.data){
					/** 重新加载数据 */
					$scope.reload();
					$scope.entity = {};
				}else{
					alert("操作失败！");
				}
		});
	};
	
	/** 显示修改，为修改表单绑定当行数据 */
	$scope.show = function(entity){
		$scope.entity = JSON.parse(JSON.stringify(entity));		
	}
	/** 批量删除 */
	$scope.delete = function(){
		if ($scope.ids.length > 0){
			/** 调用服务层 */
			baseService.deleteById("/content/delete", $scope.ids)
			.then(function(response){
					if(response.data){
						/** 重新加载数据 */
						$scope.reload();
						$scope.ids = [];
					}						
				}		
			);
		}else{
			alert("请选择要删除的数据！");
		}
	};

	/** 查询内容分类数据 */
	$scope.findContentCategoryList = function(){
		baseService.sendGet("/contentCategory/findAll")
			.then(function(response){
				$scope.contentCategoryList = response.data;
			});
	};

    /** 初始化entity*/
    $scope.entity = {};

    /** 文件上传 */
	$scope.uploadFile = function(){
        uploadService.uploadFile().then(function(response){
        	if (response.data.status == 200){
				$scope.entity.pic = response.data.url;
			}else{
        		alert("上传失败！");
			}
		});
	};
    /** 定义数组 */
    $scope.status = ['无效', '有效'];
});