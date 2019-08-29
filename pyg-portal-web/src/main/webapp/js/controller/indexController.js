/** 内容控制层 */
app.controller("indexController", function ($scope, baseService) {
    /** 根据广告分类id查询广告内容 */
    $scope.findContentByCategoryId = function (categoryId) {
        baseService.sendGet("/content/findContentByCategoryId?categoryId="
            + categoryId).then(
            function (response) {
                $scope.contentList = response.data;
            }
        );
    };
});

