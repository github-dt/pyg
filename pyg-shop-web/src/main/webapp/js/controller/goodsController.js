/** 定义控制器层 */
app.controller('goodsController', function($scope, $controller, $location,baseService,uploadService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 查询全部 */
    $scope.findAll = function(){
        baseService.sendGet("/goods/findAll").then(function(response){
            $scope.dataList = response.data;
        });
    };

    /** 定义商品状态数组 */
    $scope.status = ['未审核','已审核','审核未通过','关闭'];


    /** 定义搜索对象 */
    $scope.searchEntity = {};
    /** 分页查询 */
    $scope.search = function(page, rows){
        baseService.findByPage("/goods/search", page,
			rows, $scope.searchEntity)
            .then(function(response){
                $scope.dataList = response.data.rows;
                /** 更新总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 保存商品 */
    $scope.saveOrUpdate = function(){

        /** 定义请求URL */
        var url = "save"; // 添加
        if ($scope.goods.id){
            url = "update"; // 修改
        }


        /** 获取富文本编辑器的内容 */
        $scope.goods.goodsDesc.introduction = editor.html();

        baseService.sendPost("/goods/"+url, $scope.goods).then(
            function(response){
                if(response.data){
                    alert("保存成功！");
                    /** 清空表单 */
                    $scope.goods = {};
                    /** 清空富文本编辑器 */
                    editor.html('');

                    /** 跳转到商品列表页 */
                    location.href = "/admin/goods.html";

                }else{
                    alert("保存失败！");
                }
            }
        );
    };


    /** 显示修改 */
    $scope.show = function(entity){
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }
    };


    /**上传图片 */
    $scope.uploadFile = function(){
        uploadService.uploadFile().then(function(response) {
            /** 如果上传成功，取出url */
            if(response.data.status == 200){
                /** 设置图片访问地址 */
                $scope.image_entity.url = response.data.url;
            }else{
                alert("上传失败！");
            }
        });
    };

    /** 定义数据存储结构 */
    $scope.goods = { goodsDesc : { itemImages : []}};
    /** 添加图片到数组 */
    $scope.add_image_entity = function(){
        $scope.goods.goodsDesc.itemImages.push($scope.image_entity);
    };

    /** 数组中移除图片 */
    $scope.remove_image_entity = function(index){
        $scope.goods.goodsDesc.itemImages.splice(index, 1);
    };

    /**商品分类*/
    /** 根据父级ID查询分类 */
    $scope.findItemCatByParentId = function(parentId, name){
        baseService.sendGet("/itemCat/findItemCatByParentId",
            "parentId=" + parentId).then(function(response){
            $scope[name] = response.data;
        });
    };
    /** 监控 goods.category1Id 变量，查询二级分类 */
    /**
     * $watch方法用于监控某个变量的值，被监控的值发生变化，就自动执行相应的函数
     */
    $scope.$watch('goods.category1Id', function(newValue, oldValue){
        if (newValue){
            /** 根据选择的值查询二级分类 */
            $scope.findItemCatByParentId(newValue, "itemCatList2");
        }else{
            $scope.itemCatList2 = [];
        }
    });
    /** 监控 goods.category2Id 变量，查询三级分类 */
    $scope.$watch('goods.category2Id', function(newValue, oldValue){
        if (newValue){
            /** 根据选择的值查询三级分类 */
            $scope.findItemCatByParentId(newValue, "itemCatList3");
        }else{
            $scope.itemCatList3 = [];
        }
    });
    /** $watch():用于监听goods.category3Id变量是否发生改变 */
    $scope.$watch('goods.category3Id', function(newValue, oldValue){
        if (!newValue){ // 判断是否为undefined，如果返回
            return;
        }
        // 循环三级分类数组 List<ItemCat> : [{},{}]
        for (var i = 0; i < $scope.itemCatList3.length; i++){
            // 取一个数组元素 {}
            var itemCat = $scope.itemCatList3[i];
            // 判断id
            if (itemCat.id == newValue){
                $scope.goods.typeTemplateId = itemCat.typeId;
                break;
            }
        }
    });

    /** 监控 goods.typeTemplateId 模板ID，查询该模版对应的品牌 */
    $scope.$watch('goods.typeTemplateId',
        function(newValue, oldValue) {
            if (!newValue){
                return;
            }
            baseService.findOne("/typeTemplate/findOne", newValue)
                .then(function(response){
                    /** 获取模版中的品牌列表 */
                    $scope.brandIds = JSON.parse(response.data.brandIds);

                    /** 设置扩展属性 */
                    $scope.goods.goodsDesc.customAttributeItems =
                        JSON.parse(response.data.customAttributeItems);
                });
        });

    /** 监控 goods.typeTemplateId 模板ID */
    $scope.$watch('goods.typeTemplateId',function(newValue, oldValue) {
        if (!newValue){
            return;
        }
        /** 查询该模版对应的品牌  */
        baseService.findOne("/typeTemplate/findOne", newValue)
            .then(function(response){
                /** 获取模版中的品牌列表 */
                $scope.brandIds = JSON.parse(response.data.brandIds);
                /** 设置扩展属性 */
                if(!$location.search().id){
                    /** 设置商品描述中的扩展属性 */
                    $scope.goods.goodsDesc.customAttributeItems =
                        JSON.parse(response.data.customAttributeItems);
                }

            });

        /** 查询该模版对应的规格与规格选项 */
        baseService.findOne("/typeTemplate/findSpecByTemplateId",
            newValue)
            .then(function(response){
                $scope.specList = response.data;
            });
    });

    /** 定义数据存储结构(修改以前定义的) */
    $scope.goods = {'goodsDesc' : {'itemImages' : [], 'specificationItems':[]}};

    /** 定义修改规格选项方法 */
    $scope.updateSpecAttr = function($event, name, value){
        /** 根据json对象的key到json数组中搜索该key对应值的对象 */
        var obj = $scope.searchJsonByKey($scope.goods.goodsDesc
            .specificationItems,'attributeName', name);
        /** 判断对象是否为空 */
        if(obj){
            /** 判断checkbox是否选中 */
            if($event.target.checked){
                /** 添加该规格选项到数组中 */
                obj.attributeValue.push(value);
            }else{
                /** 取消勾选，从数组中删除该规格选项 */
                obj.attributeValue.splice(obj.attributeValue
                    .indexOf(value), 1);
                /** 如果选项都取消了，将此条记录删除 */
                if(obj.attributeValue.length == 0){
                    $scope.goods.goodsDesc.specificationItems.splice(
                        $scope.goods.goodsDesc
                            .specificationItems.indexOf(obj),1);
                }
            }
        }else{
            /** 如果为空，则新增数组元素 */
            $scope.goods.goodsDesc.specificationItems.push(
                {"attributeName":name,"attributeValue":[value]});
        }
    };

    /** 创建SKU数组 */
    $scope.createItems = function(){
        /** 定义SKU数组，并初始化 */
        $scope.goods.items = [{spec:{}, price:0, num:9999,
            status:'0', isDefault:'0' }];
        /** 定义选中的规格选项数组 */
        var specItems = $scope.goods.goodsDesc.specificationItems;
        /** 循环选中的规格选项数组 */
        for(var i = 0; i < specItems.length; i++){
            /** 扩充SKU数组方法 */
            $scope.goods.items = swapItems($scope.goods.items,
                specItems[i].attributeName,
                specItems[i].attributeValue);
        }
    };


    /** 扩充SKU数组方法 */
    var swapItems = function(items, columnName, columnValues){
        /** 创建新的SKU数组 */
        var newItems = new Array();
        /** 迭代旧的SKU数组 */
        for(var i = 0; i < items.length; i++){
            /** 获取一个SKU商品 */
            var item = items[i];
            /** 迭代规格选项值数组 */
            for(var j = 0; j < columnValues.length; j++){
                /** 克隆旧的SKU商品，产生新的SKU商品 */
                var newItem = JSON.parse(JSON.stringify(item));
                /** 增加新的key与value */
                newItem.spec[columnName] = columnValues[j];
                /** 添加到新的SKU数组 */
                newItems.push(newItem);
            }
        }
        return newItems;
    };



    /** 根据主键id查询单个商品 */
    $scope.findOne = function(){
        /** 获取id的参数值 */
        var id = $location.search().id;
        if(id == null){
            return;
        }
        baseService.findOne("/goods/findOne",id)
            .then(function(response){
                $scope.goods = response.data;

                /** 富文本编辑器添加商品介绍 */
                editor.html($scope.goods.goodsDesc.introduction);

                /** 把图片json字符串转化成图片数组 */
                $scope.goods.goodsDesc.itemImages =
                    JSON.parse($scope.goods.goodsDesc.itemImages);

                /** 把扩展属性json字符串转化成数组 */
                $scope.goods.goodsDesc.customAttributeItems =
                    JSON.parse($scope.goods.goodsDesc.customAttributeItems);

                /** 把规格json字符串转化成数组 */
                $scope.goods.goodsDesc.specificationItems =
                    JSON.parse($scope.goods.goodsDesc.specificationItems);

                /** SKU列表规格json字符串转换对象 */
                for(var i = 0; i < $scope.goods.items.length; i++){
                    $scope.goods.items[i].spec =
                        JSON.parse($scope.goods.items[i].spec);
                }



            });
    };

    /** 根据规格名称和选项名称返回是否被勾选 */
    $scope.checkAttributeValue = function(specName, optionName){
        /** 定义规格选项数组 */
        var specItems = $scope.goods.goodsDesc.specificationItems;
        /** 搜索规格选项对象 */
        var obj = $scope.searchJsonByKey(specItems,'attributeName',specName);
        if(obj){
            return obj.attributeValue.indexOf(optionName) >= 0;
        }
        return false;
    };



});