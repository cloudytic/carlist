package search;


import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

import java.util.Arrays;
import java.util.List;

public class Filterer {
    private BoolQuery.Builder filter = QueryBuilders.bool();
    public boolean has;

    public Field field(String name) {
        has = true;
        return new Field(name);
    }

    public Filterer field(String name, String value) {
        has = true;
        Field field = new Field(name);
        Filterer filterer;
        if(value.contains(",")) {
            filterer = field.in(Arrays.asList(value.split(",")));
        } else {
            filterer = field.is(value.trim());
        }
        return filterer;
    }

    public BoolQuery get() {
        return filter.build();
    }

    public class Field {
        private String key;
        public Field(String key) {
            this.key = key;
        }

        public Filterer match(Object value) {
            Query query = getTermQuery(value);
            filter.must(query);
            return Filterer.this;
        }

        public Query getTermQuery(Object value) {
            Query query;
            if(value instanceof String) {
                query = QueryBuilders.match(t->t.field(key).query((String)value));
            } else if(value instanceof Boolean) {
                query = QueryBuilders.match(t->t.field(key).query((Boolean) value));
            } else if(value instanceof Integer) {
                query = QueryBuilders.match(t->t.field(key).query((Integer)value));
            } else if(value instanceof Long) {
                query = QueryBuilders.match(t->t.field(key).query((Long)value));
            } else if(value instanceof Float) {
                query = QueryBuilders.match(t->t.field(key).query((Float)value));
            } else if(value instanceof Double) {
                query = QueryBuilders.match(t->t.field(key).query((Double)value));
            } else {
                throw new RuntimeException("Unknown type: " + value.getClass());
            }
            return query;
        }
        
        public Query getTermQuery2(Object value) {
            Query query;
            if(value instanceof String) {
                query = QueryBuilders.term(t->t.field(key).value((String)value));
            } else if(value instanceof Boolean) {
                query = QueryBuilders.term(t->t.field(key).value((Boolean) value));
            } else if(value instanceof Integer) {
                query = QueryBuilders.term(t->t.field(key).value((Integer)value));
            } else if(value instanceof Long) {
                query = QueryBuilders.term(t->t.field(key).value((Long)value));
            } else if(value instanceof Float) {
                query = QueryBuilders.term(t->t.field(key).value((Float)value));
            } else if(value instanceof Double) {
                query = QueryBuilders.term(t->t.field(key).value((Double)value));
            } else {
                throw new RuntimeException("Unknown type: " + value.getClass());
            }

            return query;
        }

        public Filterer is(Object value) {
            Query query = getTermQuery(value);
            filter.must(query);
            return Filterer.this;
        }
        public Filterer not(Object value) {
            Query query = getTermQuery(value);
            filter.mustNot(query);
            return Filterer.this;
        }
        public Filterer from(String value) {
            filter.must(QueryBuilders.range(t->t.field(key).from(value)));
            return Filterer.this;
        }
        public Filterer to(String value) {
            filter.must(QueryBuilders.range(t->t.field(key).to(value)));
            return Filterer.this;
        }
        public Filterer range(String start, String end) {
            filter.must(QueryBuilders.range(t->t.field(key).from(start).to(end)));
            return Filterer.this;
        }
        public Filterer in(Object[] array) {
            if(array.length > 0) {
                BoolQuery bool = QueryBuilders.bool().build();
                for (Object value : array) {
                    Query query = getTermQuery(value);
                    bool.should().add(query);
                }
                filter.must(bool._toQuery());
            }
            return Filterer.this;
        }
        public Filterer in(List<String> array) {
            if(array.size() > 0) {
                BoolQuery bool = QueryBuilders.bool().build();
                for (String value : array) {
                    bool.should().add(QueryBuilders.term(t->t.field(key).value(value)));
                }
                filter.must(bool._toQuery());
            }
            return Filterer.this;
        }
        public Filterer empty() {
            filter.mustNot(QueryBuilders.exists().field(key).build()._toQuery());
            return Filterer.this;
        }
        public Filterer notEmpty() {
            filter.must(QueryBuilders.exists().field(key).build()._toQuery());
            return Filterer.this;
        }
    }

    private Filterer() {}

    public static Filterer start() {
        return new Filterer();
    }
}
