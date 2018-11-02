package org.chrisli.utils.tree;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.chrisli.utils.Assert;
import org.chrisli.utils.cache.EhcacheUtil;
import org.chrisli.utils.reflect.ReflectUtil;
import org.chrisli.utils.tree.annotation.TreeNodeChildrenList;
import org.chrisli.utils.tree.annotation.TreeNodeId;
import org.chrisli.utils.tree.annotation.TreeNodeParentId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * [树相关的工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class TreeUtil {

    public static final String TREE_PROPERTY_CACHE_KEY_FORMAT = "TREE_PROPERTY_[%s]";

    /**
     * [初始化树VO,将数据集合转换为树对象]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static <T> T initTree(T root, List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return root;
        }
        TreeProperty property = getTreeProperty(root.getClass());
        // 递归挑选中孩子,直到所有数据完成
        pickOutChildren(property, root, dataList);
        return root;
    }

    /**
     * [从树对象集合中获取父节点下所有相关id]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static <T> String getRelatedIds(List<T> dataList, String parentId) {
        if (CollectionUtils.isEmpty(dataList)) {
            return parentId;
        }
        T root = dataList.get(0);
        TreeProperty property = getTreeProperty(root.getClass());
        StringBuilder builder = new StringBuilder();
        builder.append(parentId);
        // 设置根节点的主键
        ReflectUtil.setFieldValue(root, property.getTreeNodeIdFieldName(), parentId);
        pickOutChildren(builder, property, root, dataList);
        return builder.toString();
    }

    /**
     * [获取源对象中配置的树属性]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private static TreeProperty getTreeProperty(Class originClass) {
        Assert.notNull(originClass);
        String cacheKey = String.format(TREE_PROPERTY_CACHE_KEY_FORMAT, originClass.getName());
        Object cacheValue = EhcacheUtil.get(cacheKey);
        if (cacheValue != null) {
            return (TreeProperty) cacheValue;
        }
        // 缓存中没有,重新获取
        TreeProperty treeProperty = new TreeProperty();
        Field[] fields = ReflectUtil.getAllFields(originClass, Object.class);
        for (Field field : fields) {
            if (field.isAnnotationPresent(TreeNodeId.class)) {
                treeProperty.setTreeNodeIdFieldName(field.getName());
            }
            if (field.isAnnotationPresent(TreeNodeParentId.class)) {
                treeProperty.setTreeNodeParentIdFieldName(field.getName());
            }
            if (field.isAnnotationPresent(TreeNodeChildrenList.class)) {
                treeProperty.setTreeNodeChildrenListFieldName(field.getName());
            }
        }
        Assert.notBlank(treeProperty.getTreeNodeIdFieldName(), String.format("Class[%s] has no field which is annotation present by '@TreeNodeId' !", originClass.getName()));
        Assert.notBlank(treeProperty.getTreeNodeParentIdFieldName(), String.format("Class[%s] has no field which is annotation present by '@TreeNodeParentId' !", originClass.getName()));
        Assert.notBlank(treeProperty.getTreeNodeChildrenListFieldName(), String.format("Class[%s] has no field which is annotation present by '@TreeNodeChildrenList' !", originClass.getName()));
        // 存入缓存,便于下次调用
        EhcacheUtil.put(cacheKey, treeProperty);
        return treeProperty;
    }

    /**
     * [从数据集合中挑选中对应的子对象,存放到树节点的子集集合中]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private static <T> void pickOutChildren(TreeProperty property, T parent, List<T> dataList) {
        String id = ReflectUtil.getFieldValue(parent, property.getTreeNodeIdFieldName()).toString();
        List<T> childrenList = (List<T>) ReflectUtil.getFieldValue(parent, property.getTreeNodeChildrenListFieldName());
        // 如果集合为空,则初始化一个空集合
        if (childrenList == null) {
            childrenList = new ArrayList<T>();
        }
        Iterator<T> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            T data = iterator.next();
            String parentId = ReflectUtil.getFieldValue(data, property.getTreeNodeParentIdFieldName()).toString();
            if (parentId.equals(id)) {
                // 添加下阶的数据
                childrenList.add(data);
                // 完成后删除该数据
                iterator.remove();
            }
        }
        ReflectUtil.setFieldValue(parent, property.getTreeNodeChildrenListFieldName(), childrenList);
        // 本次循环完成后,处理每个子集的数据
        for (T child : childrenList) {
            pickOutChildren(property, child, dataList);
        }
    }

    /**
     * [从数据集合中挑选中对应的孩子]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private static <T> void pickOutChildren(StringBuilder builder, TreeProperty property, T parent, List<T> dataList) {
        String id = ReflectUtil.getFieldValue(parent, property.getTreeNodeIdFieldName()).toString();
        List<T> childrenList = (List<T>) ReflectUtil.getFieldValue(parent, property.getTreeNodeChildrenListFieldName());
        // 如果集合为空,则初始化一个空集合
        if (childrenList == null) {
            childrenList = new ArrayList<T>();
        }
        Iterator<T> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            T data = iterator.next();
            String parentId = ReflectUtil.getFieldValue(data, property.getTreeNodeParentIdFieldName()).toString();
            if (parentId.equals(id)) {
                // 添加下阶的数据
                childrenList.add(data);
                // 完成后删除该数据
                iterator.remove();
                builder.append(",").append(ReflectUtil.getFieldValue(data, property.getTreeNodeIdFieldName()).toString());
                // 完成后删除该数据
                iterator.remove();
            }
        }
        ReflectUtil.setFieldValue(parent, property.getTreeNodeChildrenListFieldName(), childrenList);
        // 本次循环完成后,处理每个子集的数据
        for (T child : childrenList) {
            pickOutChildren(builder, property, child, dataList);
        }
    }
}