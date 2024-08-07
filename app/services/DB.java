package services;

import controllers.Util;
import models.ModelBase;
import models.ModelInterface;
import play.db.jpa.JPAApi;
import pojos.Param;

import javax.inject.Inject;
import javax.inject.Singleton;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by seyi
 */
@Singleton
public class DB {
	public static JPAApi jpa;

	@Inject
	public DB(JPAApi jpaApi) {
		jpa = jpaApi;
	}

	public static <T extends ModelBase> void save(T model) {
		jpa.withTransaction(em -> {
			if(model.getId() == null) {
				em.persist(model);
			} else {
				em.merge(model);
			}
		});
	}

	public static <T> void persist(T model) {
		jpa.withTransaction(em -> {
			em.persist(model);
		});
	}

	public static <T> void merge(T model) {
		jpa.withTransaction(em -> {
			em.merge(model);
		});
	}

	public static <T extends ModelInterface> Long count(Class<T> clazz, Filter filter) {
		AtomicReference<Long> t = new AtomicReference<>(0L);
		jpa.withTransaction(em -> {
			String sql = "SELECT COUNT(t) FROM " + clazz.getSimpleName() + " t ";
			sql += filter.getSql();
			TypedQuery<Long> q = em.createQuery(sql, Long.class);
			addParam(q, filter);
			t.set(q.getSingleResult());
		});
		return t.get();
	}

	public static <T extends ModelInterface> List<T> find(Class<T> clazz, Filter filter) {
		List<T> list = new ArrayList<>();
		jpa.withTransaction(em -> {
			String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
			sql += filter.getSql();
			TypedQuery<T> q = em.createQuery(sql, clazz);
			addParam(q, filter);
			list.addAll(q.getResultList());
		});
		return list;
	}

	public static <T extends ModelInterface> List<T> find(Class<T> clazz, Filter filter, Param param) {
		List<T> list = new ArrayList<>();
		jpa.withTransaction(em -> {
			String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
			sql += filter.getSql();
			sql = addSortClause(clazz, sql, param, filter);
			TypedQuery<T> q = em.createQuery(sql, clazz);
			addParam(q, filter);
			q.setFirstResult(param.getOffset()).setMaxResults(param.getSize());
			list.addAll(q.getResultList());
		});
		return list;
	}

	public static <T extends ModelInterface> List<T> findD(Class<T> clazz, Filter filter, Param param) {
		List<T> list = new ArrayList<>();
		jpa.withTransaction(em -> {
			String sql = "SELECT DISTINCT t FROM " + clazz.getName() + " t ";
			sql += filter.getSql();
			sql = addSortClause(clazz, sql, param, filter);
			TypedQuery<T> q = em.createQuery(sql, clazz);
			addParam(q, filter);
			q.setFirstResult(param.getOffset()).setMaxResults(param.getSize());
			list.addAll(q.getResultList());
		});
		return list;
	}

	private static <T extends ModelInterface> String addSortClause(Class<T> clazz, String sql, Param param, Filter filter) {
		String sort = param.getSort();
		if(Util.isNotEmpty(sort)) {

			//Todo: SANITIZE SORT TO PREVENT SQL INJECTION
			// ONLY ALPHANUMERIC AND _ CHARS ALLOWED
			// NEED A BETTER APPROACH
			sort = sort.replaceAll("[^A-Za-z0-9_]", "");

			sql += " ORDER BY " + sort;
			String order = param.getOrder();
			if(Arrays.asList("asc", "desc").contains(order)) {
				sql += " " + order;
			}
		}
		return sql;
	}

	public static <T extends ModelInterface> T findOne(Class<T> clazz, Long id) {
		AtomicReference<T> ref = new AtomicReference<>(null);
		try {
			jpa.withTransaction(em -> {
				T t = em.find(clazz, id);
				ref.set(t);
			});
		} catch(NoResultException e) {
		} catch(Exception e) {e.printStackTrace();}
		return ref.get();
	}

	public static <T extends ModelInterface> T findOne(Class<T> clazz, String id) {
		try {
			return findOne(clazz, Long.valueOf(id));
		} catch (Exception e) {
			return null;
		}
	}

	public static <T extends ModelInterface> T findOne(Class<T> clazz, Filter filter) {
		AtomicReference<T> ref = new AtomicReference<>(null);
		try {
			jpa.withTransaction(em -> {
				String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
				sql += filter.getSql();
				TypedQuery<T> q = em.createQuery(sql, clazz);
				addParam(q, filter);
				ref.set(q.setMaxResults(1).getSingleResult());
			});
		} catch(NoResultException e) {
		} catch(Exception e) {e.printStackTrace();}
		return ref.get();
	}

	public static <T extends ModelInterface> T findOne(Class<T> clazz, Filter filter, Param param) {
		List<T> list =  find(clazz, filter, Param.getOne(param.getSort(), param.getOrder()));
		if(!list.isEmpty()) return list.get(0);
		return null;
	}

	/*public static <T extends ModelInterface> T findOne(Class<T> clazz, String key, Object value) {
		AtomicReference<T> ref = new AtomicReference<>(null);
		try {
			jpa.withTransaction(em -> {
				 T t = em.createQuery("SELECT t FROM "+clazz.getSimpleName()+" t WHERE t."+key+"=:value", clazz)
					.setParameter("value", value)
					.setMaxResults(1)
					.getSingleResult();
					ref.set(t);
			});
		} catch(NoResultException e) {
		} catch(Exception e) {e.printStackTrace();}
		return ref.get();
	}*/

	public static <T extends ModelInterface> T findRandOne(Class<T> clazz, Filter filter) {
		AtomicReference<T> ref = new AtomicReference<>(null);
		try {
			jpa.withTransaction(em -> {
				String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
				sql += filter.getSql();
				sql += " ORDER BY RAND()";
				TypedQuery<T> q = em.createQuery(sql, clazz);
				addParam(q, filter);
				q.setMaxResults(1);
				ref.set(q.getSingleResult());
			});
		} catch(NoResultException e) {
		} catch(Exception e) {e.printStackTrace();}
		return ref.get();
	}

	public static <T> void delete(Class<T> clazz, String id){
		jpa.withTransaction(em -> {
			em.remove(em.find(clazz, id));
		});
	}

	public static <T> void delete(T t){
		jpa.withTransaction(em -> {
			em.remove(t);
		});
	}

	public static <T extends ModelInterface> void delete(Class<T> clazz, Long id){
		jpa.withTransaction(em -> {
			T t = em.find(clazz, id);
			em.remove(t);
		});
	}

	public static <T> void addParam(TypedQuery<T> q, Filter filter) {
		for(Map.Entry<String, Object> entry: filter.getParams().entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof Date) {
				q.setParameter(key, (Date)value, TemporalType.TIMESTAMP);
			} else {
				q.setParameter(key, value);
			}
		}
	}
	
	public static Filter where() {
		return Filter.get();
	}
	
	public static class Filter {
		private StringBuilder sql = new StringBuilder();
		private Map<String, Object> params = new HashMap<>();

		public Field field(String name) {
			return new Field(name);
		}

		public Filter field(String name, Object value) {
			return new Field(name).eq(value);
		}

		private Filter where() {
			sql.append(" WHERE ");
			return this;
		}

		public Filter join(String join) {
			sql.append(" JOIN ").append(join).append(" ");
			return this;
		}

		public Filter or() {
			sql.append(" OR ");
			return this;
		}

		public Filter brS() {
			sql.append(" ( ");
			return this;
		}

		public Filter brE() {
			sql.append(" ) AND ");
			return this;
		}

		protected String getSql() {
			String s = sql.toString();
			s = s.trim();
			if(s.endsWith("AND"))
				s = s.substring(0, s.length()-3);
			if(s.endsWith("OR"))
				s = s.substring(0, s.length()-2);
			if(s.endsWith("WHERE"))
				s = s.substring(0, s.length()-5);
			s = s.replaceAll("AND +OR", "OR")
					.replaceAll("AND +\\)", ")")
					.replaceAll("OR +\\)", ")");
			return s;
		}

		protected Map<String, Object> getParams() {
			return params;
		}

		private Filter() {}

		private Filter(StringBuilder sql, Map<String, Object> params) {
			this.sql = sql;
			this.params = params;
		}

		public Filter copy() {
			return new Filter(new StringBuilder(sql.toString()), new HashMap<>(params));
		}

		public class Field {
			private String name;
			public Field(String name) {
				this.name = name;
			}

			public Filter eq(Object value) {
				sql.append(name).append(" = :").append(key()).append(" AND ");
				params.put(key(), value);
				return Filter.this;
			}
			public Filter ne(Object value) {
				String key = key() + value.toString().replaceAll("[^A-Za-z]", "");
				sql.append(name).append(" != :").append(key).append(" AND ");
				params.put(key, value);
				return Filter.this;
			}
			public Filter like(String value) {
				sql.append(name).append(" LIKE :").append(key()).append(" AND ");
				params.put(key(), "%"+value+"%");
				return Filter.this;
			}
			public Filter gt(Object value) {
				sql.append(name).append(" > :").append(key()).append(" AND ");
				params.put(key(), value);
				return Filter.this;
			}
			public Filter lt(Object value) {
				sql.append(name).append(" < :").append(key()).append(" AND ");
				params.put(key(), value);
				return Filter.this;
			}
			public Filter gte(Object value) {
				sql.append(name).append(" >= :").append(key()).append(" AND ");
				params.put(key(), value);
				return Filter.this;
			}
			public Filter lte(Object value) {
				sql.append(name).append(" <= :").append(key()).append(" AND ");
				params.put(key(), value);
				return Filter.this;
			}
			public Filter isNull() {
				sql.append(name).append(" IS NULL AND ");
				return Filter.this;
			}
			public Filter notNull() {
				sql.append(name).append(" IS NOT NULL AND ");
				return Filter.this;
			}
			public Filter contains(Object value) {
				sql.append(" '").append(value).append("' MEMBER OF ").append(name).append(" AND ");
				return Filter.this;
			}
			public Filter between(Object start, Object end) {
				String startKey = key()+"Start";
				String endKey = key()+"End";
				sql.append(name).append(" BETWEEN :").append(startKey).append(" AND :").append(endKey).append(" AND ");
				params.put(startKey, start);
				params.put(endKey, end);
				return Filter.this;
			}
			public Filter in(List<Object> array) {
				String key = key()+"Array";
				sql.append(name).append(" IN :").append(key).append(" AND ");
				params.put(key, array);
				return Filter.this;
			}
			public Filter nin(List<Object> array) {
				String key = key()+"Array";
				sql.append(name).append(" NOT IN :").append(key).append(" AND ");
				params.put(key, array);
				return Filter.this;
			}
			private String key() {
				return name.replaceAll("[^A-Za-z]", "");
			}
		}

		protected static Filter get() {
			Filter filter = new Filter();
			return filter.where();
		}
	}

}
