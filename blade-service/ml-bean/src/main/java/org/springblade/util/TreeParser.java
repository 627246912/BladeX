package org.springblade.util;


import org.springblade.bean.TreeEntity;
import org.springblade.core.tool.utils.Func;
import org.springblade.interfaces.IFilterTreeEntity;

import java.util.*;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description: 解析树形工具类
 */
public class TreeParser {

    /**
     * 解析树形数据
     * @param topId
     * @param entityList
     * @return
     * @author wangqiaoqing
     */
    public static <E extends TreeEntity<E>> List<E> getTreeList(String topId, List<E> entityList, boolean needSort) {
        List<E> resultList=new ArrayList<>();

        Map<String,List<E>> entityMap = new HashMap<>();
        //获取顶层元素集合
        String parentId;
        List<E> entitys;
        for (E entity : entityList) {
            parentId=entity.getParentIdStr();
            if(parentId==null||topId.equals(parentId)){
                resultList.add(entity);
            }
            parentId = StringUtils.nvl(parentId,"0");
            if(entityMap.containsKey(parentId)) {
                entityMap.get(parentId).add(entity);
            }else{
                entitys = new ArrayList<>();
                entityMap.put(parentId,entitys);
                entitys.add(entity);
            }
        }

        Map<String,Object> childResultMap;
        //获取每个顶层元素的子数据集合
        for (E entity : resultList) {
            childResultMap = getSubList(entity.getIdStr(), entityMap,needSort);
            entity.setChildList((List<E>) childResultMap.get("list"));
            entity.setChildBizCount((Integer) childResultMap.get("count"));
        }

        if(needSort){
            Collections.sort(resultList, new Comparator<E>() {
                @Override
                public int compare(E o1, E o2) {
                    return o1.getSortNo() - o2.getSortNo();
                }
            });
        }

        return resultList;
    }

    /**
     * 解析树形数据
     * @param rank
     * @param entityList
     * @return
     * @author wangqiaoqing
     */
    public static <E extends TreeEntity<E>> List<E> getTreeListByRankId(Integer rank, List<E> entityList, boolean needSort) {
        List<E> resultList=new ArrayList<>();

        Map<String,List<E>> entityMap = new HashMap<>();
        //获取顶层元素集合
        String parentId;
        List<E> entitys;
        for (E entity : entityList) {
            parentId=entity.getParentIdStr();
            if(entity.getRank().intValue() == rank.intValue()){
                resultList.add(entity);
            }
            parentId = StringUtils.nvl(parentId,"0");
            if(entityMap.containsKey(parentId)) {
                entityMap.get(parentId).add(entity);
            }else{
                entitys = new ArrayList<>();
                entityMap.put(parentId,entitys);
                entitys.add(entity);
            }
        }

        Map<String,Object> childResultMap;
        //获取每个顶层元素的子数据集合
        for (E entity : resultList) {
            childResultMap = getSubList(entity.getIdStr(), entityMap,needSort);
            entity.setChildList((List<E>) childResultMap.get("list"));
            entity.setChildBizCount((Integer) childResultMap.get("count"));
        }

        if(needSort){
            Collections.sort(resultList, new Comparator<E>() {
                @Override
                public int compare(E o1, E o2) {
                    return o1.getSortNo() - o2.getSortNo();
                }
            });
        }

        return resultList;
    }

    /**
     * 获取子数据集合
     * @param id
     * @return
     * @author wangqiaoqing
     */
    private  static  <E extends TreeEntity<E>>  Map<String,Object> getSubList(String id, Map<String,List<E>> entityMap,boolean needSort) {
        List<E> childList=new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        String parentId;
        Integer childCount = 0;

        List<E> entityList = entityMap.get(id);
        if(StringUtils.isNotEmpty(entityList)) {
            //子集的直接子对象
            for (E entity : entityList) {
                parentId = entity.getParentIdStr();
                if (id.equals(parentId)) {
                    childCount = childCount + (StringUtils.isEmpty(entity.getBizCount()) ? 0 : entity.getBizCount());
                    childList.add(entity);
                }
            }
        }
        entityMap.remove(id);

        Map<String,Object> childResultMap;
        //子集的间接子对象
        for (E entity : childList) {
            childResultMap = getSubList(entity.getIdStr(), entityMap,needSort);
            entity.setChildList((List<E>) childResultMap.get("list"));
            entity.setChildBizCount((Integer) childResultMap.get("count"));
        }

        //递归退出条件
        if(childList.size()==0){
            result.put("list",null);
            result.put("count",0);
            return result;
        }

        if(needSort){
            Collections.sort(childList, new Comparator<E>() {
                @Override
                public int compare(E o1, E o2) {
                    return o1.getSortNo() - o2.getSortNo();
                }
            });
        }

        result.put("list",childList);
        result.put("count",childCount);
        return result;
    }


    /**
     * 获取子数据集合
     * @param id
     * @param entityList
     * @return
     * @author wangqiaoqing
     */
    private  static  <E extends TreeEntity<E>>  Map<String,Object> getSubList(String id, List<E> entityList,boolean needSort) {
        List<E> childList=new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        String parentId;
        Integer childCount = 0;

        //子集的直接子对象
        for (E entity : entityList) {
            parentId=entity.getParentIdStr();
            if(id.equals(parentId)){
                childCount = childCount + (StringUtils.isEmpty(entity.getBizCount())?0:entity.getBizCount());
                childList.add(entity);
            }
        }
        Map<String,Object> childResultMap;
        //子集的间接子对象
        for (E entity : childList) {
            childResultMap = getSubList(entity.getIdStr(), entityList,needSort);
            entity.setChildList((List<E>) childResultMap.get("list"));
            entity.setChildBizCount((Integer) childResultMap.get("count"));
        }

        //递归退出条件
        if(childList.size()==0){
            result.put("list",null);
            result.put("count",0);
            return result;
        }

        if(needSort){
            Collections.sort(childList, new Comparator<E>() {
                @Override
                public int compare(E o1, E o2) {
                    return o1.getSortNo() - o2.getSortNo();
                }
            });
        }

        result.put("list",childList);
        result.put("count",childCount);
        return result;
    }


    /**
     * 获取子id集合
     * @param entityList
     * @param curEntity
     * @param <E>
     * @return
     */
    public static <E extends TreeEntity<E>> List<String> getChildIdList(List<E> entityList,E curEntity) {
        List<String> childIdList=new ArrayList<>();
        getSubIdList(curEntity.getIdStr(),entityList,childIdList);
        return childIdList;
    }

    /**
     * 获取子数据集合
     * @param id
     * @param entityList
     * @return
     * @author wangqiaoqing
     */
    public static  <E extends TreeEntity<E>>  void getSubIdList(String id, List<E> entityList,List<String> childIdList) {
        List<E> childList=new ArrayList<>();
        String parentId;

        //子集的直接子对象
        for (E entity : entityList) {
            parentId=entity.getParentIdStr();
            if(id.equals(parentId)){
                childList.add(entity);
            }
        }

        //子集的间接子对象
        for (E entity : childList) {
            getSubIdList(entity.getIdStr(), entityList,childIdList);
        }

        childIdList.add(id);
        //递归退出条件
        if(childList.size()==0){
            return;
        }
    }

    /**
     * 获取父级名称拼接
     * @param id
     * @param map
     * @param spliceSign
     * @param <E>
     * @return
     */
    public static <E extends TreeEntity<E>> String getParentStr(String id,Map<String,E> map,String spliceSign){
        StringBuffer sbf = new StringBuffer();
        if(StringUtils.isNotEmpty(map)) {
            getParent(id, map, sbf, spliceSign);
        }
        String result = "";
        if(sbf.length()>0){
            result = sbf.substring(0,sbf.length()-1);
        }

        return result;
    }

    /**
     * 递归获取父级
     * @param parentId
     * @param map
     * @param sbf
     * @param spliceSign
     * @param <E>
     * @return
     */
    private static <E extends TreeEntity<E>> void getParent(String parentId,Map<String,E> map,StringBuffer sbf,String spliceSign){
        if(StringUtils.isEmpty(parentId) || "0".equals(parentId)){
            return;
        }

        E e = map.get(parentId);
        if(StringUtils.isNotNull(e)) {
            sbf.insert(0, spliceSign);
            sbf.insert(0, e.getNameStr());
            getParent(e.getParentIdStr(), map, sbf, spliceSign);
        }
    }


    /**
     * 过滤并且返回树
     * @param topIdStr
     * @param entityList
     * @param needSort
     * @param params
     * @param <E>
     * @return
     */
    public static <E extends TreeEntity<E>> List<E> getTreeListByFilter(String topIdStr,List<E> entityList,boolean needSort,String params) {
		topIdStr= Func.isEmpty(topIdStr)==true?"0":topIdStr;
        Map<String,E> allMap = new HashMap<>();
        Map<String,E> existsMap = new HashMap<>();
        List<E> treeList = new ArrayList<>();
        List<E> tempList = new ArrayList<>();

        for (E e : entityList) {
            allMap.put(e.getIdStr(),e);

            if(e.filterByParam(params)){
                treeList.add(e);
                tempList.add(e);
                existsMap.put(e.getIdStr(),e);
            }
        }


        if(StringUtils.isNotEmpty(treeList)){
            for (E e : treeList) {
                //递归获取父级
                addParentToList(topIdStr, allMap, existsMap, tempList,e);
            }
            treeList = getTreeList(topIdStr,tempList,needSort);
        }
        return treeList;
    }


    /**
     * 过滤并且返回树
     * @param rank
     * @param entityList
     * @param needSort
     * @param params
     * @param <E>
     * @return
     */
    public static <E extends TreeEntity<E>> List<E> getRankTreeListByFilter(Integer rank,List<E> entityList,boolean needSort,String params) {
        Map<String,E> allMap = new HashMap<>();
        Map<String,E> existsMap = new HashMap<>();
        List<E> treeList = new ArrayList<>();
        List<E> tempList = new ArrayList<>();

        for (E e : entityList) {
            allMap.put(e.getIdStr(),e);

            if(e.filterByParam(params)){
                treeList.add(e);
                tempList.add(e);
                existsMap.put(e.getIdStr(),e);
            }
        }


        if(StringUtils.isNotEmpty(treeList)){
            for (E e : treeList) {
                //递归获取父级
                addParentToListByRank(rank, allMap, existsMap, tempList,e);
            }
            treeList = getTreeListByRankId(rank,tempList,needSort);
        }
        return treeList;
    }


    /**
     * 过滤并且返回树
     * @param topIdStr
     * @param entityList
     * @param needSort
     * @return
     */
    public static <E extends TreeEntity<E>> List<E> getTreeListByFilter(String topIdStr, List<E> entityList, boolean needSort, IFilterTreeEntity filterTreeEntity) {
        Map<String,E> allMap = new HashMap<>();
        Map<String,E> existsMap = new HashMap<>();
        List<E> treeList = new ArrayList<>();
        List<E> tempList = new ArrayList<>();

        for (E e : entityList) {
            allMap.put(e.getIdStr(),e);

            if(filterTreeEntity.filter(e)){
                treeList.add(e);
                tempList.add(e);
                existsMap.put(e.getIdStr(),e);
            }
        }


        if(StringUtils.isNotEmpty(treeList)){
            for (E e : treeList) {
                //递归获取父级
                addParentToList(topIdStr, allMap, existsMap, tempList,e);
            }
            treeList = getTreeList(topIdStr,tempList,needSort);
        }
        return treeList;
    }


    /**
     * 过滤并且返回树
     * @param rank
     * @param entityList
     * @param needSort
     * @param <E>
     * @return
     */
    public static <E extends TreeEntity<E>> List<E> getRankTreeListByFilter(Integer rank, List<E> entityList, boolean needSort, IFilterTreeEntity filterTreeEntity) {
        Map<String,E> allMap = new HashMap<>();
        Map<String,E> existsMap = new HashMap<>();
        List<E> treeList = new ArrayList<>();
        List<E> tempList = new ArrayList<>();

        for (E e : entityList) {
            allMap.put(e.getIdStr(),e);

            if(filterTreeEntity.filter(e)){
                treeList.add(e);
                tempList.add(e);
                existsMap.put(e.getIdStr(),e);
            }
        }


        if(StringUtils.isNotEmpty(treeList)){
            for (E e : treeList) {
                //递归获取父级
                addParentToListByRank(rank, allMap, existsMap, tempList,e);
            }
            treeList = getTreeListByRankId(rank,tempList,needSort);
        }
        return treeList;
    }

    /**
     * 递归添加父级
     * @param topIdStr
     * @param allMap
     * @param existsMap
     * @param entityList
     * @param <E>
     */
    private static <E extends TreeEntity<E>> void addParentToList(String topIdStr,Map<String,E> allMap, Map<String,E> existsMap, List<E> entityList,E e) {
        if(StringUtils.isNotEmpty(e.getParentIdStr()) && !e.getParentIdStr().equals(topIdStr)){
            if(!existsMap.containsKey(e.getParentIdStr())) {
                E parentE = allMap.get(e.getParentIdStr());
                if(StringUtils.isNotNull(parentE)) {
                    existsMap.put(e.getParentIdStr(), parentE);
                    entityList.add(parentE);
                    addParentToList(topIdStr, allMap, existsMap, entityList, parentE);
                }
            }
        }
    }

    /**
     * 递归添加父级
     * @param rank
     * @param allMap
     * @param existsMap
     * @param entityList
     * @param <E>
     */
    private static <E extends TreeEntity<E>> void addParentToListByRank(Integer rank,Map<String,E> allMap, Map<String,E> existsMap, List<E> entityList,E e) {
        if(StringUtils.isNotEmpty(e.getParentIdStr()) && e.getRank().intValue() != rank.intValue()){
            if(!existsMap.containsKey(e.getParentIdStr())) {
                E parentE = allMap.get(e.getParentIdStr());
                if(StringUtils.isNotNull(parentE)) {
                    existsMap.put(e.getParentIdStr(), parentE);
                    entityList.add(parentE);
                    addParentToListByRank(rank, allMap, existsMap, entityList, parentE);
                }
            }
        }
    }

}
