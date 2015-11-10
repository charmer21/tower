package com.tower.service.cache.dao.ibatis;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.tower.service.cache.dao.ICacheVersionDAO;
import com.tower.service.cache.dao.ibatis.mapper.CacheVersionMapper;
import com.tower.service.cache.dao.model.CacheVersion;
import com.tower.service.dao.ibatis.AbsStrIDIBatisDAOImpl;
import com.tower.service.dao.ibatis.IBatisDAOException;
import com.tower.service.dao.ibatis.ISMapper;
import com.tower.service.dao.ibatis.SqlmapUtils;
import com.tower.service.exception.DataAccessException;

@Repository("CacheVersion")
public class CacheVersionIbatisDAOImpl extends AbsStrIDIBatisDAOImpl<CacheVersion> implements
    ICacheVersionDAO<CacheVersion> {

  @Resource(name = "cacheSessionFactory")
  private SqlSessionFactory masterSessionFactory;

  @Resource(name = "cacheSlaveSessionFactory")
  private SqlSessionFactory slaveSessionFactory;

  @Resource(name = "cacheMapQuerySessionFactory")
  private SqlSessionFactory mapQuerySessionFactory;

  public CacheVersionIbatisDAOImpl() {
  }

  @Override
  public Class<CacheVersionMapper> getMapperClass() {

    return CacheVersionMapper.class;
  }

  @Override
  public Class<CacheVersion> getModelClass() {

    return CacheVersion.class;
  }

  @Override
  public boolean isFk(String property) {

    return CacheVersion.isFk(property);
  }

  public String getTableName() {
    return "soa_cache_version";
  }

  @Override
	public SqlSessionFactory getMasterSessionFactory(){
		return masterSessionFactory;
	}
	
	
	@Override
	public SqlSessionFactory getSlaveSessionFactory(){
		if (slaveSessionFactory == null) {
			return getMasterSessionFactory();
		}
		return slaveSessionFactory;
	}
	
	@Override
	public SqlSessionFactory getMapQuerySessionFactory(){
		if (mapQuerySessionFactory == null) {
			return getSlaveSessionFactory();
		}
		return mapQuerySessionFactory;
	}

  public String insert(CacheVersion model, String tabNameSuffix) {
    if (logger.isDebugEnabled()) {
      logger.debug("insert(T model={}, String tabNameSuffix={}) - start", model, tabNameSuffix); //$NON-NLS-1$
    }

    validate(model);

    model.setTKjtTabName(this.get$TKjtTabName(tabNameSuffix));

    SqlSessionFactory sessionFactory = this.getMasterSessionFactory();
    SqlSession session = SqlmapUtils.openSession(sessionFactory);
    try {
      ISMapper<CacheVersion> mapper = session.getMapper(getMapperClass());

      Long id = mapper.insert(model);
      if (id != null) {
        this.incrTabVersion(tabNameSuffix);
      }

      if (logger.isDebugEnabled()) {
        logger
            .debug(
                "insert(T model={}, String tabNameSuffix={}) - end - return value={}", model, tabNameSuffix, id); //$NON-NLS-1$
      }
      return model.getId();
    } catch (Exception t) {
      logger.error("insert(T, String)", t); //$NON-NLS-1$
      throw new DataAccessException(IBatisDAOException.MSG_2_0001, t);
    } finally {
    	SqlmapUtils.release(session,sessionFactory);
    }
  }

  @Cacheable(value = "defaultCache", key = CacheKeyPrefixExpress, unless = "#result == null", condition = "#root.target.cacheable()")
  @Override
  public CacheVersion queryById(String id, String tabNameSuffix) {
    return super.queryById(id, tabNameSuffix);
  }

  @Cacheable(value = "defaultCache", key = CacheKeyPrefixExpress + "", unless = "#result == null", condition = "!#master and #root.target.cacheable()")
  @Override
  public CacheVersion queryById(String id, Boolean master, String tabNameSuffix) {
    return super.queryById(id, master, tabNameSuffix);
  }

  @CacheEvict(value = "defaultCache", key = CacheKeyPrefixExpress, condition = "#root.target.cacheable()")
  public int incrObjVersion(String id, String tabNameSuffix) {
    validate(id);
    CacheVersion model = new CacheVersion();
    model.setId(id);
    model.setTKjtTabName(this.get$TKjtTabName(tabNameSuffix));
    SqlSessionFactory sessionFactory = this.getMasterSessionFactory();
    SqlSession session = SqlmapUtils.openSession(sessionFactory);
    try {
      CacheVersionMapper mapper = session.getMapper(getMapperClass());
      Integer eft = mapper.incrVersion(model);

      return eft;
    } catch (Exception t) {
      throw new DataAccessException(IBatisDAOException.MSG_2_0001, t);
    } finally {
    	SqlmapUtils.release(session,sessionFactory);
    }
  }

  @CacheEvict(value = "defaultCache", key = CacheKeyPrefixExpress, condition = "#root.target.cacheable()")
  public int incrObjRecVersion(String id, String tabNameSuffix) {
    validate(id);

    CacheVersion model = new CacheVersion();
    model.setId(id);
    model.setTKjtTabName(this.get$TKjtTabName(tabNameSuffix));

    SqlSessionFactory sessionFactory = this.getMasterSessionFactory();
    SqlSession session = SqlmapUtils.openSession(sessionFactory);
    try {
      CacheVersionMapper mapper = session.getMapper(getMapperClass());
      Integer eft = mapper.incrRecVersion(model);

      return eft;
    } catch (Exception t) {
      throw new DataAccessException(IBatisDAOException.MSG_2_0001, t);
    } finally {
    	SqlmapUtils.release(session,sessionFactory);
    }
  }

  @CacheEvict(value = "defaultCache", key = CacheKeyPrefixExpress, condition = "#root.target.cacheable()")
  public int incrObjTabVersion(String id, String tabNameSuffix) {
    validate(id);

    CacheVersion model = new CacheVersion();
    model.setId(id);
    model.setTKjtTabName(this.get$TKjtTabName(tabNameSuffix));

    SqlSessionFactory sessionFactory = this.getMasterSessionFactory();
    SqlSession session = SqlmapUtils.openSession(sessionFactory);
    try {
      CacheVersionMapper mapper = session.getMapper(getMapperClass());
      Integer eft = mapper.incrTabVersion(model);

      return eft;
    } catch (Exception t) {
      throw new DataAccessException(IBatisDAOException.MSG_2_0001, t);
    } finally {
    	SqlmapUtils.release(session,sessionFactory);
    }
  }

  protected void validate(String id) {
    if (id == null || id.trim().length() == 0) {
      throw new DataAccessException(IBatisDAOException.MSG_1_0004);
    }
  }
}
