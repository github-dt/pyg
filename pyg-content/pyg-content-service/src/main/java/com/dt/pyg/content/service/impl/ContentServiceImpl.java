package com.dt.pyg.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dt.pyg.common.pojo.PageResult;
import com.dt.pyg.content.service.ContentService;
import com.dt.pyg.mapper.ContentMapper;
import com.dt.pyg.pojo.Content;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 内容服务接口实现类
 *
 */
@Service(interfaceName = "com.dt.pyg.content.service.ContentService")
@Transactional
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /** 分页查询广告 */
    public PageResult findByPage(int page, int rows){
        try{
            /** 开始分页 */
            PageInfo<Content> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            contentMapper.selectAll();
                        }
                    });
            /** 创建PageResult */
            PageResult pageResult = new PageResult();
            pageResult.setTotal(pageInfo.getTotal());
            pageResult.setRows(pageInfo.getList());
            return pageResult;
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /** 添加广告 */
    public void saveContent(Content content){
        try{
            contentMapper.insertSelective(content);

            try {
                /** 清除Redis缓存 */
                redisTemplate.delete("content");
            }catch(Exception ex){ex.printStackTrace();}

        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /** 修改广告 */
    public void updateContent(Content content){
        try{
            contentMapper.updateByPrimaryKeySelective(content);

            try {
                /** 清除Redis缓存 */
                redisTemplate.delete("content");
            }catch(Exception ex){ex.printStackTrace();}

        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /** 删除广告 */
    public void deleteContent(Long[] ids){
        try{
            for (Long id : ids){
                contentMapper.deleteByPrimaryKey(id);
            }

            try {
                /** 清除Redis缓存 */
                redisTemplate.delete("content");
            }catch (Exception ex){ex.printStackTrace();}

        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Content> findContentByCategoryId(Long categoryId) {
        /** 定义广告数据 */
        List<Content> contentList = null;
        try{
            contentList = (List<Content>) redisTemplate.boundValueOps("content").get();
            if (contentList != null && contentList.size() > 0){
                System.out.println("========Redis中获取缓存数据============" + contentList);
                return contentList;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try{

            // 创建示范对象
            Example example = new Example(Content.class);
            // 创建查询条件对象
            Example.Criteria criteria = example.createCriteria();
            // 添加等于条件 category_id = categoryId
            criteria.andEqualTo("categoryId", categoryId);
            // 添加等于条件 status = 1
            criteria.andEqualTo("status", "1");
            // 排序
            example.orderBy("sortOrder").asc();

            contentList = contentMapper.selectByExample(example);

            /** ############# 把广告数据存储到Redis中 ############## */
            try {
                // Spring-Data-Redis: 它会把contentList集合转化成字节进行存储
                redisTemplate.boundValueOps("content").set(contentList);
                //设置key30秒后过期
                redisTemplate.expire("content", 30, TimeUnit.SECONDS);
                Long contentTime = redisTemplate.getExpire("content");
                System.out.println(contentTime);
                System.out.println("======把广告数据存储到Redis中=========");
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return contentList;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}