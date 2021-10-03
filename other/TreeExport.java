package com.datadriver.core.generic;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.datadriver.core.entity.UnitedLogger;

/**
 * @author
 * 2018年4月10日下午9:02:06
 * TODO 树状菜单     使用流程   1 传入对象集合    2  传入匹配规则 ruleParams  3 调用
 */
public class TreeExport {
    //id
    private Integer exportId;
    //父 id，用来构造父子关系
    private Integer exportPid;
    //级别
    private Integer exportLevel;
    /**
     * 第一个String 传入对象属性，第二个String 为true表示属性不为空，  false表示为空，其他表示属性  equals 值
     */
    private List<Map<String, String>> ruleParams;
    //导出的对象集合
    private List<? extends TreeExport> objectList;

    /**
     * @param list
     * @return 根据父ID进行排序
     */
    public static <T extends TreeExport> List<T> sortByParentId(List<T> list) {
        List<T> resultList = new ArrayList<T>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getExportId() == null || list.get(i).getExportPid() == null) {
                UnitedLogger.error("====================>>> 集合中第" + (i + 1) + "个参数没有exportId/exportPid属性，请检查集合参数或使用generationOfPaternity()方法。");
                return list;
            }
            if (list.get(i).getExportPid() == 0) {
                resultList.add(list.get(i));
            }
            addNodeByParentId(resultList, list);
        }
        return resultList;
    }
    /*private static<T extends TreeExport>void addNodeByParentId(List<T> parentList,List<T>currentList){
        for (int j = 0; j < parentList.size(); j++) {
            //获取父级最后一个节点
            T parentNode = parentList.get(j);
            for (int i = 0; i < currentList.size(); i++) {
                T currentNode = currentList.get(i);
                if(currentNode.getExportPid() == parentNode.getExportId()){
                    //添加到返回的集合
                    parentList.add(currentNode);
                    //递归获取子节点
                    addNodeByParentId(parentList,currentList);
                }
            }
        }

    }*/

    private static <T extends TreeExport> void addNodeByParentId(List<T> parentList, List<T> currentList) {
        T parentNode = parentList.get(parentList.size() - 1);

        for (int i = 0; i < currentList.size(); ++i) {
            T currentNode = currentList.get(i);
            if (currentNode.getExportPid() == parentNode.getExportId()) {
                parentList.add(currentNode);
                addNodeByParentId(parentList, currentList);
            }
        }

    }

    /**
     * @return 对当前集合进行排序     第一步  设置 TreeExport.ruleParams  第二步 设置 TreeExport.objectList
     * 第三步 调用方法进行排序
     */
    @SuppressWarnings("unchecked")
    public <T extends TreeExport> List<T> generationOfPaternity() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
        //第一步  循环传入的参数，根据参数判断当前对象的级别并按级别存入一个新的集合
        //如果没有传入参数，则不需要进行处理
        List<T> reslultList = null;
        if (ruleParams == null || objectList == null) {
            return reslultList;
        }
        try {
            //给当前集合加入级别
            List<List<T>> levelList = new ArrayList<List<T>>();
            //保存匹配不到的数据
            List<T> ortherList = new ArrayList<T>();
            //创建分级集合
            for (int i = 0; i < ruleParams.size(); i++) {
                List<T> tempList = new ArrayList<T>();
                levelList.add(tempList);
            }
            for (int i = 0; i < objectList.size(); i++) {
                boolean flag = false;
                for (int j = 0; j < ruleParams.size(); j++) {
                    //判断对象是否为当前级别
                    if (isCurrentLevel(ruleParams.get(j), objectList.get(i))) {
                        //添加到临时级别集合
                        levelList.get(j).add((T) objectList.get(i));
                        objectList.get(i).setExportLevel(j + 1);
                        flag = true;
                        break;
                    }
                }
                //匹配不到
                if (!flag) {
                    ortherList.add((T) objectList.get(i));
                }
            }
            //第二步  循环新的集合并根据条件添加子集
            levelList = setIdPid(levelList);
            //第三步  排序刷新
            reslultList = exportSort(levelList);
            //第四部  添加没有匹配上的组合
            if (ortherList.size() > 0) {
                reslultList.addAll(ortherList);
            }
        } catch (NoSuchFieldException e) {
            UnitedLogger.error("构建树形菜单出错!没有可取的子属性！");
            UnitedLogger.error(e.getMessage(), e);
        } catch (SecurityException e) {
            UnitedLogger.error("构建树形菜单出错!安全验证异常!");
            UnitedLogger.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            UnitedLogger.error("构建树形菜单出错!参数转换异常!");
            UnitedLogger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            UnitedLogger.error("构建树形菜单出错!安全权限异常!");
            UnitedLogger.error(e.getMessage(), e);
        }
        System.out.println(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
        return reslultList;
    }

    /**
     * @param levelList
     * @return 根据级别及对应的规则生产父子关系
     * @throws SecurityException        安全异常
     * @throws NoSuchFieldException     没有可用属性异常
     * @throws IllegalAccessException   没有权限异常
     * @throws IllegalArgumentException 参数转换异常
     */
    private <T extends TreeExport> List<List<T>> setIdPid(List<List<T>> levelList) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        //循环父级集合
        int index = 1;
        for (int i = 0; i < levelList.size(); i++) {
            //循环当前集合下的明细
            for (int j = 0; j < levelList.get(i).size(); j++) {
                T parentNode = levelList.get(i).get(j);
                parentNode.setExportId(index);
                if (i == 0) {
                    parentNode.setExportPid(0);
                }
                if (i < levelList.size() - 1) {
                    //循环下级明细并添加到当前明细下
                    for (int k = 0; k < levelList.get(i + 1).size(); k++) {
                        T currentNode = levelList.get(i + 1).get(k);
                        Map<String, String> ruleMap = ruleParams.get(i);
                        if (isCurrentChild(parentNode, currentNode, ruleMap)) {
                            currentNode.setExportPid(index);
                        }
                    }
                }
                index++;
            }
        }
        return levelList;
    }

    /**
     * @param levelList 分级集合
     * @return 循环分级集合，并递归添加到返回的集合
     * @throws NoSuchFieldException     没有可用属性异常
     * @throws IllegalAccessException   没有权限异常
     * @throws IllegalArgumentException 参数转换异常
     * @throwsSecurityException 安全异常
     */
    private <T extends TreeExport> List<T> exportSort(List<List<T>> levelList) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<T> resultList = new ArrayList<T>();
        for (int i = 0; i < levelList.size(); i++) {
            resultList.addAll(levelList.get(i));
        }
        resultList = sortByParentId(resultList);
        return resultList;
    }

    /**
     * @param parentNode  当前要插入的父节点
     * @param currentNode 循环的节点
     * @param ruleMap     当前级别的处理条件
     * @return 是否为当前父节点的子节点
     * @throws SecurityException        安全异常
     * @throws NoSuchFieldException     没有可用属性异常
     * @throws IllegalAccessException   没有权限异常
     * @throws IllegalArgumentException 参数转换异常
     */
    private <T extends TreeExport> boolean isCurrentChild(T parentNode, T currentNode, Map<String, String> ruleMap) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        boolean flag = true;
        //获取父类
        Class<? extends TreeExport> tempClass = parentNode.getClass();
        for (String tempRule : ruleMap.keySet()) {
            //获取属性值
            String value = ruleMap.get(tempRule);
            if (!"false".equalsIgnoreCase(value)) {
                //获取属性名，根据属性名使用反射获取当前属性名对应的属性值
                Field tempField = tempClass.getDeclaredField(tempRule);
                //设置可以获取私有属性
                tempField.setAccessible(true);
                flag = tempField.get(parentNode).equals(tempField.get(currentNode));
            }
            if (!flag) {
                return flag;
            }
        }

        return flag;
    }

    /**
     * @param ruleMap    当前级别的规则
     * @param treeExport 当前对象
     * @return 根据当前规则判断对象是否为当前级别
     * @throws SecurityException        安全异常
     * @throws NoSuchFieldException     没有可用属性异常
     * @throws IllegalAccessException   没有权限异常
     * @throws IllegalArgumentException 参数转换异常
     */
    private <T extends TreeExport> boolean isCurrentLevel(Map<String, String> ruleMap, T treeExport) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        boolean flag = true;
        Class<? extends TreeExport> tempClass = treeExport.getClass();
        for (String tempRule : ruleMap.keySet()) {
            //获取属性名，根据属性名使用反射获取当前属性名对应的属性值
            Field field = tempClass.getDeclaredField(tempRule);
            //设置可以获取私有属性
            field.setAccessible(true);
            Object obj = field.get(treeExport);
            if ("true".equalsIgnoreCase(ruleMap.get(tempRule))) {
                flag = obj != null;
            } else if ("false".equalsIgnoreCase(ruleMap.get(tempRule))) {

                flag = obj == null;
            } else {
                flag = obj.equals(ruleMap.get(tempRule));
            }
            if (!flag) {
                return flag;
            }
        }
        return flag;
    }

    public Integer getExportId() {
        return exportId;
    }

    public void setExportId(Integer exportId) {
        this.exportId = exportId;
    }

    public Integer getExportPid() {
        return exportPid;
    }

    public void setExportPid(Integer exportPid) {
        this.exportPid = exportPid;
    }

    public Integer getExportLevel() {
        return exportLevel;
    }

    public void setExportLevel(Integer exportLevel) {
        this.exportLevel = exportLevel;
    }

    public List<Map<String, String>> getRuleParams() {
        return ruleParams;
    }

    public void setRuleParams(List<Map<String, String>> ruleParams) {
        this.ruleParams = ruleParams;
    }

    public List<? extends TreeExport> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<? extends TreeExport> objectList) {
        this.objectList = objectList;
    }


}

