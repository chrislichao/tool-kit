package org.chrisli.mybatis.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.chrisli.mybatis.query.Where;
import org.chrisli.utils.exception.FrameworkException;

import java.util.List;

/**
 * [基础服务类,提供基础的增删查改功能]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public abstract class BaseService<T extends BaseEntity> {
    /**
     * 错误信息
     */
    protected String ERROR_MSG_FORMAT = "Expected one result (or null) to be returned by %s(), but found: %d ";

    /**
     * [获取对应的dao]
     */
    protected abstract BaseDao<T> getBaseDao();

    /**
     * [基类新增方法]
     */
    public int create(T t) {
        return getBaseDao().create(t);
    }

    /**
     * [基类的删除方法,通过主键删除数据]
     */
    public int deleteByPk(T t) {
        return getBaseDao().deleteByPk(t);
    }

    /**
     * [基类的批量删除方法,根据对象属性(查询条件不包括值为空的属性)删除]
     */
    public int deleteBatch(T t) {
        return getBaseDao().deleteBatch(t);
    }

    /**
     * [基类的批量删除方法,根据对象属性(查询条件包括值为空的属性)删除]
     */
    public int deleteBatchByAllFields(T t) {
        return getBaseDao().deleteBatchByAllFields(t);
    }

    /**
     * [基类的批量删除方法,自定义条件批量删除]
     */
    public int deleteBatch(Where where) {
        return getBaseDao().deleteBatchWhere(where);
    }

    /**
     * [基类的修改方法,根据主键,修改单个对象的属性,不包含值为空的字段]
     */
    public int updateByPk(T t) {
        return getBaseDao().updateByPk(t);
    }

    /**
     * [基类的修改方法,根据主键,修改单个对象的属性,包含值为空的字段]
     */
    public int updateAllFieldsByPk(T t) {
        return getBaseDao().updateAllFieldsByPk(t);
    }

    /**
     * [基类的修改方法,根据自定义查询条件,修改单个对象的属性,不包含值为空的字段]
     */
    public int updateBatch(T t, Where where) {
        return getBaseDao().updateWhere(t, where);
    }

    /**
     * [基类的修改方法,根据自定义查询条件,修改单个对象的属性,包含值为空的字段]
     */
    public int updateAllFieldsBatch(T t, Where where) {
        return getBaseDao().updateAllFieldsWhere(t, where);
    }

    /**
     * [基类的查询方法,通过主键查询,最多只能查到一个对象返回,如果查到多个,则抛异常]
     */
    public T retrieveByPk(T t) {
        return getBaseDao().retrieveByPk(t);
    }

    /**
     * [基类的查询方法,根据对象属性(查询条件不包括值为空的属性)查到满足条件的单个对象]
     */
    public T retrieveOne(T t) {
        List<T> list = getBaseDao().retrieveList(t);
        if (list.size() > 1) {
            throw new FrameworkException(String.format(ERROR_MSG_FORMAT, "retrieveOne", list.size()));
        }
        return list.size() == 1 ? list.get(0) : null;
    }

    /**
     * [基类的查询方法,根据对象属性(查询条件包括值为空的属性)查到满足条件的单个对象]
     */
    public T retrieveOneByAllFields(T t) {
        List<T> list = getBaseDao().retrieveListByAllFields(t);
        if (list.size() > 1) {
            throw new FrameworkException(String.format(ERROR_MSG_FORMAT, "retrieveOneByAllFields", list.size()));
        }
        return list.size() == 1 ? list.get(0) : null;
    }

    /**
     * [基类的查询方法,根据自定义查询条件查到满足条件的单个对象]
     */
    public T retrieveOneWhere(Where where) {
        List<T> list = getBaseDao().retrieveListWhere(where);
        if (list.size() > 1) {
            throw new FrameworkException(String.format(ERROR_MSG_FORMAT, "retrieveOneWhere", list.size()));
        }
        return list.size() == 1 ? list.get(0) : null;
    }

    /**
     * [基类的查询方法,查询所有对象]
     */
    public List<T> retrieveAll(Class<T> clazz) {
        try {
            List<T> objList = getBaseDao().retrieveList(clazz.newInstance());
            return objList;
        } catch (Exception e) {
            throw new FrameworkException("Get clazz[" + clazz.getName() + "] newInstance error!");
        }
    }

    /**
     * [基类的查询方法,根据对象属性(查询条件不包括值为空的属性)查到对象集合]
     */
    public List<T> retrieveList(T t) {
        return getBaseDao().retrieveList(t);
    }

    /**
     * [基类的查询方法,根据对象属性(查询条件包括值为空的属性)查到对象集合]
     */
    public List<T> retrieveListByAllFields(T t) {
        return getBaseDao().retrieveListByAllFields(t);
    }

    /**
     * [基类的查询方法,根据自定义查询条件查到对象集合,如有排序,在where中定义]
     */
    public List<T> retrieveListWhere(Where where) {
        return getBaseDao().retrieveListWhere(where);
    }

    /**
     * [基类的分页查询方法,根据对象属性(查询条件不包括值为空的属性)查到对象集合]
     */
    public PageInfo<T> retrievePageList(T t, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<T>(retrieveList(t));
    }

    /**
     * [基类的分页查询方法,根据对象属性(查询条件包括值为空的属性)查到对象集合]
     */
    public PageInfo<T> retrievePageListByAllFields(T t, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<T>(retrieveListByAllFields(t));
    }

    /**
     * [基类的分页查询方法,根据自定义查询条件查到对象集合]
     */
    public PageInfo<T> retrievePageListWhere(Where where, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<T>(retrieveListWhere(where));
    }

    /**
     * [基类的查询个数的方法,根据对象属性(查询条件不包括值为空的属性)查到对象个数]
     */
    public int getCount(T t) {
        return getBaseDao().getCount(t);
    }

    /**
     * [基类的查询个数方法,根据对象属性(查询条件包括值为空的属性)查到对象个数]
     */
    public int getCountByAllFields(T t) {
        return getBaseDao().getCountByAllFields(t);
    }

    /**
     * [基类的查询方法,根据自定义查询条件查到对象集合,如有排序,在where中定义]
     */
    public int getCountWhere(Where where) {
        return getBaseDao().getCountWhere(where);
    }
}
