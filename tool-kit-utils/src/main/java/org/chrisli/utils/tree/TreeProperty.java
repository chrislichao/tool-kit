package org.chrisli.utils.tree;

/**
 * [树对象属性]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class TreeProperty {
    /**
     * 树节点id字段名
     */
    private String treeNodeIdFieldName;
    /**
     * 树节点父id字段名
     */
    private String treeNodeParentIdFieldName;
    /**
     * 树节点孩子集合字段名
     */
    private String treeNodeChildrenListFieldName;

    public String getTreeNodeIdFieldName() {
        return treeNodeIdFieldName;
    }

    public void setTreeNodeIdFieldName(String treeNodeIdFieldName) {
        this.treeNodeIdFieldName = treeNodeIdFieldName;
    }

    public String getTreeNodeParentIdFieldName() {
        return treeNodeParentIdFieldName;
    }

    public void setTreeNodeParentIdFieldName(String treeNodeParentIdFieldName) {
        this.treeNodeParentIdFieldName = treeNodeParentIdFieldName;
    }

    public String getTreeNodeChildrenListFieldName() {
        return treeNodeChildrenListFieldName;
    }

    public void setTreeNodeChildrenListFieldName(String treeNodeChildrenListFieldName) {
        this.treeNodeChildrenListFieldName = treeNodeChildrenListFieldName;
    }
}