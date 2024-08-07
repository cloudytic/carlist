package search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import controllers.Util;
import formatters.DateFormatter;
import lombok.extern.slf4j.Slf4j;
import models.Account;
import models.Auto;
import models.BlogArticle;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import pojos.BlogSearch;
import pojos.Page;
import pojos.Param;
import pojos.SellerContact;

import java.io.IOException;
import java.util.*;

@Slf4j
public class Searcher {
    private static final ElasticsearchClient esClient;

    public static final String AUTO_INDEX = "cl_autos";
    public static final String ACCOUNT_INDEX = "cl_accounts";
    public static final String S_CONTACT_INDEX = "cl_s_contacts";
    public static final String BLOG_INDEX = "cl_blogs";

    static {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        esClient = new ElasticsearchClient(transport);
    }
    public static void indexAuto(Auto auto) {
        try {
            IndexResponse response = esClient.index(i -> i
                    .index(AUTO_INDEX)
                    .id(auto.getId().toString())
                    .document(auto)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Auto getAuto(Long id) {
        GetResponse<Auto> response;
        try {
            response = esClient.get(g -> g
                            .index(AUTO_INDEX)
                            .id(id.toString()),
                    Auto.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (response.found()) {
            Auto auto = response.source();
            log.info("Auto ref " + auto.getRef());
            return auto;
        } else {
            log.info("Auto not found");
            throw new RuntimeException("Auto not found");
        }
    }

    public static SearchRes autoSearch(SearchReq req, Param param, boolean withAgg) {
        SearchRequest request = SearchRequest.of(s -> {
            SearchRequest.Builder builder = s.index(AUTO_INDEX)
                .query(q -> {
                    if (Util.isEmpty(req.search)) {
                        return q.matchAll(t -> t);
                    } else {
                        return q.match(t -> t
                                .field("search")
                                .query(req.search)
                        );
                    }
                });


            if(Arrays.asList("price", "year", "created").contains(param.getSort())) {
                if ("desc".equals(param.getOrder())) {
                    builder = builder.sort(o -> o.field(f -> f.field(param.getSort()).order(SortOrder.Desc)));
                } else {
                    builder = builder.sort(o -> o.field(f -> f.field(param.getSort()).order(SortOrder.Asc)));
                }
            } else {
                builder = builder.sort(o -> o.field(f -> f.field("boostedOn").order(SortOrder.Desc)));
            }


            if(withAgg) {

                SearchReq cReq = req.copy();
                cReq.condition = null;
                builder = builder.aggregations("condition", f -> f.filter(h -> h.bool(getFilterer(cReq).get()))
                        .aggregations("condition", a -> a.terms(h -> h.field("condition.slug"))));

                SearchReq bReq = req.copy();
                bReq.bodyType = null;
                builder = builder.aggregations("bodyType", f -> f.filter(h -> h.bool(getFilterer(bReq).get()))
                        .aggregations("bodyType", a -> a.terms(h -> h.field("bodyType.slug").size(Integer.MAX_VALUE))
                        ));

                SearchReq mReq = req.copy();
                mReq.model = null;
                builder = builder.aggregations("make", f -> f.filter(h -> h.bool(getFilterer(mReq).get()))
                        .aggregations("make", a -> a.terms(h -> h.field("model.parent.slug").size(Integer.MAX_VALUE))
                                .aggregations("model", b -> b.terms(h -> h.field("model.slug").size(Integer.MAX_VALUE))
                                )));


                SearchReq lReq = req.copy();
                lReq.locality = null;
                builder = builder.aggregations("state", a -> a.filter(h -> h.bool(getFilterer(lReq).get()))
                        .aggregations("state", b -> b.terms(h -> h.field("locality.parent.slug").size(Integer.MAX_VALUE))
                                .aggregations("locality", c -> c.terms(h -> h.field("locality.slug").size(Integer.MAX_VALUE))
                                )));
            }

            return builder.from(param.getOffset())
                    .size(param.getSize())
                    .postFilter(getFilterer(req).get()._toQuery()).trackTotalHits(t -> t.enabled(true));

        });


        SearchRes res = new SearchRes();
        try {
            SearchResponse<Auto> response = esClient.search(request, Auto.class);
            TotalHits total = response.hits().total();

            if(withAgg) {
                response.aggregations().forEach((field, aggregate) -> {
                    res.aggregates.put(field, processAggregates(aggregate));
                });
            }

            List<Hit<Auto>> hits = response.hits().hits();

            List<Auto> autos = new ArrayList<>();

            for (Hit<Auto> hit: hits) {
                Auto auto = hit.source();
                autos.add(auto);
            }

            res.results = new Page<>(autos, total.value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    private static SearchRes resSiteMap;

    public static SearchRes siteMap() {
        if(resSiteMap != null) {
            return resSiteMap;
        }
        SearchReq req = new SearchReq();
        req.trashed = false;
        SearchRequest request = SearchRequest.of(s -> {
            SearchRequest.Builder builder = s.index(AUTO_INDEX)
                    .query(q -> q.matchAll(t -> t));

            builder = builder.aggregations("make", f -> f.filter(h -> h.bool(getFilterer(req).get())).aggregations("make", a -> a.terms(h -> h.field("model.parent.slug"))
                            .aggregations("model", b -> b.terms(h -> h.field("model.slug")))));

            builder = builder.aggregations("state", f -> f.filter(h -> h.bool(getFilterer(req).get())).aggregations("state", b -> b.terms(h -> h.field("locality.parent.slug"))
                            .aggregations("locality", c -> c.terms(h -> h.field("locality.slug")))));

            return builder;
        });


        resSiteMap = new SearchRes();
        try {
            SearchResponse<Auto> response = esClient.search(request, Auto.class);
            response.aggregations().forEach((field, aggregate) -> {
                resSiteMap.aggregates.put(field, processAggregates(aggregate));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resSiteMap;
    }

    public static List<SearchRes.Agg> processAggregates(Aggregate aggregate) {
        List<SearchRes.Agg> aggregates = new LinkedList<>();
        Object obj = aggregate._get();
        if(obj instanceof FilterAggregate filter) {
            filter.aggregations().forEach((field, child) -> {
                aggregates.addAll(processAggregates(child));
            });
        } else {
            if (obj instanceof StringTermsAggregate terms) {
                for (StringTermsBucket bucket : terms.buckets().array()) {
                    SearchRes.Agg agg = new SearchRes.Agg();
                    agg.setTerm(bucket.key().stringValue());
                    agg.setCount(bucket.docCount());
                    aggregates.add(agg);

                    if (bucket.aggregations() != null) {
                        bucket.aggregations().forEach((field, child) -> {
                            agg.setChildren(processAggregates(child));
                        });
                    }
                }
            }
        }
        return  aggregates;
    }

    public static void indexAccount(Account account) {
        try {
            IndexResponse response = esClient.index(i -> i
                    .index(ACCOUNT_INDEX)
                    .id(account.getId().toString())
                    .document(account)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SearchRes accountSearch(SearchReq req, Param param, boolean withAgg) {
        SearchRequest request = SearchRequest.of(s -> {
            SearchRequest.Builder builder = s.index(ACCOUNT_INDEX)
                    .query(q -> {
                        if (Util.isEmpty(req.search)) {
                            return q.matchAll(t -> t);
                        } else {
                            return q.match(t -> t
                                    .field("search")
                                    .query(req.search)
                            );
                        }
                    });

            builder = builder.sort(o -> o.field(f -> f.field("created").order(SortOrder.Desc)));

            req.searcheable = true;

            req.active = true;

            if(withAgg) {
                SearchReq lReq = req.copy();
                lReq.locality = null;
                builder = builder.aggregations("state", a -> a.filter(h -> h.bool(getFilterer(lReq).get()))
                        .aggregations("state", b -> b.terms(h -> h.field("locality.parent.slug"))
                                .aggregations("locality", c -> c.terms(h -> h.field("locality.slug"))
                                )));
            }

            return builder.from(param.getOffset())
                    .size(param.getSize())
                    .postFilter(getFilterer(req).get()._toQuery()).trackTotalHits(t -> t.enabled(true));

        });


        SearchRes res = new SearchRes();
        try {
            SearchResponse<Account> response = esClient.search(request, Account.class);
            TotalHits total = response.hits().total();

            if(withAgg) {
                response.aggregations().forEach((field, aggregate) -> {
                    res.aggregates.put(field, processAggregates(aggregate));
                });
            }

            List<Hit<Account>> hits = response.hits().hits();

            List<Account> accounts = new ArrayList<>();

            for (Hit<Account> hit: hits) {
                Account account = hit.source();
                accounts.add(account);
            }

            res.resultsAccounts = new Page<>(accounts, total.value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }


    public static Filterer getFilterer(SearchReq req) {
        req.init();

        Filterer filter = Filterer.start();

        if(req.hasAccount != null) {
            if(req.hasAccount) {
                filter.field("account").notEmpty();
            } else {
                filter.field("account").empty();
            }
        }

        if(req.visible != null) {
            if(req.visible) {
                filter.field("notVisible").is(false);
            } else {
                filter.field("notVisible").is(true);
            }
        }

        if(req.active != null) {
            if(req.active) {
                filter.field("active").is(true);
            } else {
                filter.field("active").is(false);
            }
        }

        if(req.searcheable != null) {
            if(req.searcheable) {
                filter.field("searcheable").is(true);
            } else {
                filter.field("searcheable").is(false);
            }
        }

        if(req.published != null) {
            if(req.published) {
                filter.field("published").is(true);
            } else {
                filter.field("published").is(false);
            }
        }

        if(req.trashed != null) {
            if(req.trashed) {
                filter.field("trashed").is(true);
            } else {
                filter.field("trashed").is(false);
            }
        }

        if(req.accountId != null) {
            filter.field("account.id").is(req.accountId);
        }

        if(Util.isNotEmpty(req.make)) {
            filter.field("model.parent.slug").is(req.make);
        }
        if(req.makeId != null) {
            filter.field("model.parent.id").is(req.makeId);
        }

        if(Util.isNotEmpty(req.model)) {
            filter.field("model.slug").is(req.model);
        }
        if(req.modelId != null) {
            filter.field("model.id").is(req.modelId);
        }

        if(Util.isNotEmpty(req.condition)) {
            filter.field("condition.slug").is(req.condition);
        }
        if(req.conditionId != null) {
            filter.field("condition.id").is(req.conditionId);
        }

        if(Util.isNotEmpty(req.transmission)) {
            filter.field("transmission.slug").is(req.transmission);
        }
        if(req.transmissionId != null) {
            filter.field("transmission.id").is(req.transmissionId);
        }

        if(Util.isNotEmpty(req.bodyType)) {
            filter.field("bodyType.slug").is(req.bodyType);
        }
        if(req.bodyTypeId != null) {
            filter.field("bodyType.id").is(req.bodyTypeId);
        }

        if(Util.isNotEmpty(req.fuelType)) {
            filter.field("fuelType.slug").is(req.fuelType);
        }
        if(req.fuelTypeId != null) {
            filter.field("fuelType.id").is(req.fuelTypeId);
        }

        if(Util.isNotEmpty(req.colorExterior)) {
            filter.field("colorExterior.slug").is(req.colorExterior);
        }
        if(req.colorExteriorId != null) {
            filter.field("colorExterior.id").is(req.colorExteriorId);
        }

        if(Util.isNotEmpty(req.colorInterior)) {
            filter.field("colorInterior.slug").is(req.colorInterior);
        }
        if(req.colorInteriorId != null) {
            filter.field("colorInterior.id").is(req.colorInteriorId);
        }

        if(req.year != null) {
            filter.field("year").is(req.year);
        }

        if(Util.isNotEmpty(req.state)) {
            filter.field("locality.parent.slug", req.state);
        }
        if(req.stateId != null) {
            filter.field("locality.parent.id").is(req.stateId);
        }

        if(Util.isNotEmpty(req.locality)) {
            filter.field("locality.slug", req.locality);
        }
        if(req.localityId != null) {
            filter.field("locality.id").is(req.localityId);
        }

        if(Util.isNotEmpty(req.priceRange)) {
            String[] priceRange = req.priceRange.split("-");
            if(priceRange.length == 2) {
                filter.field("price").range(priceRange[0], priceRange[1]);
            }
        }

        if(Util.isNotEmpty(req.priceUp)) {
            filter.field("price").from(req.priceUp);
        }

        if(req.mileageRange != null) {
            String[] mileageRange = req.mileageRange.split("-");
            if(mileageRange.length == 2) {
                filter.field("mileage").range(mileageRange[0], mileageRange[1]);
            }
        }

        if(req.yearRange != null) {
            String[] yearRange = req.yearRange.split("-");
            if(yearRange.length == 2) {
                if(Util.isNumeric(yearRange[0]) && Util.isNumeric(yearRange[1])) {
                    filter.field("year").range(yearRange[0], yearRange[1]);
                }
            }
        }

        if(Util.isNotEmpty(req.feature)) {
            filter.field("features").match(req.feature);
        }
        if(!req.features.isEmpty()){
            filter.field("features.slug").in(req.features);
        }
        try {
            if(Util.isNotEmpty(req.start_date) && Util.isNotEmpty(req.end_date)) {
                Date end_date = DateUtils.addDays(DateFormatter.convert(req.end_date), 1);
                filter.field("created").range(
                        String.valueOf(DateFormatter.convert(req.start_date).getTime()),
                        String.valueOf(end_date.getTime())
                );
            } else if(Util.isNotEmpty(req.start_date)) {
                filter.field("created").from(String.valueOf(DateFormatter.convert(req.start_date).getTime()));
            } else if(Util.isNotEmpty(req.end_date)) {
                Date end_date = DateUtils.addDays(DateFormatter.convert(req.end_date), 1);
                filter.field("created").to(String.valueOf(end_date.getTime()));
            }
        } catch (Exception e) {e.printStackTrace();}


        if(Util.isNotEmpty(req.contactPhone)) {
            filter.field("contact.phone", req.contactPhone);
        }

        if(Util.isNotEmpty(req.f)) {
            String f = req.f;
            if(f.equals("photo")) {
                filter.field("imageCount").from("1");
            }
        }

        return filter;
    }

    public static void indexSellerContact(SellerContact sellerContact) {
        try {
            IndexResponse response = esClient.index(i -> i
                    .index(S_CONTACT_INDEX)
                    .id(sellerContact.getPhone())
                    .document(sellerContact)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SellerContact getSellerContact(String phone) {
        GetResponse<SellerContact> response;
        try {
            response = esClient.get(g -> g
                            .index(S_CONTACT_INDEX)
                            .id(phone),
                    SellerContact.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (response.found()) {
            return response.source();
        } else {
            log.info("Auto not found");

            return null;
        }
    }

    public static SearchRes sellerContacts(Param param) {
        SearchRequest request = SearchRequest.of(s -> {
            SearchRequest.Builder builder = s.index(S_CONTACT_INDEX)
                    .query(q -> q.matchAll(t -> t));

            builder = builder.sort(o -> o.field(f -> f.field("autoCount").order(SortOrder.Desc)));

            return builder.from(param.getOffset())
                    .size(param.getSize()).trackTotalHits(t -> t.enabled(true));

        });


        SearchRes res = new SearchRes();
        try {
            SearchResponse<SellerContact> response = esClient.search(request, SellerContact.class);
            TotalHits total = response.hits().total();

            List<Hit<SellerContact>> hits = response.hits().hits();

            List<SellerContact> contacts = new ArrayList<>();

            for (Hit<SellerContact> hit: hits) {
                SellerContact contact = hit.source();
                contacts.add(contact);
            }

            res.resultSellerContacts = new Page<>(contacts, total.value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public static SearchRes autoSellerContactsAgg() {
        SearchRequest request = SearchRequest.of(s -> {
            SearchRequest.Builder builder = s.index(AUTO_INDEX)
                    .query(q -> q.matchAll(t -> t));

            builder = builder.aggregations("contact", a -> a.terms(h -> h.field("contact.phone")
                    .size(Integer.MAX_VALUE)));
            return builder;
        });

        SearchRes res = new SearchRes();
        try {
            SearchResponse<Auto> response = esClient.search(request, Auto.class);
            response.aggregations().forEach((field, aggregate) -> {
                res.aggregates.put(field, processAggregates(aggregate));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public static void indexBlogArticle(BlogArticle blogArticle) {
        try {
            IndexResponse response = esClient.index(i -> i
                    .index(BLOG_INDEX)
                    .id(blogArticle.getId().toString())
                    .document(blogArticle)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SearchRes blogPosts(BlogSearch req, Param param) {
        SearchRequest request = SearchRequest.of(s -> {
            SearchRequest.Builder builder = s.index(BLOG_INDEX)
                    .query(q -> {
                        if (Util.isEmpty(req.search)) {
                            return q.matchAll(t -> t);
                        } else {
                            return q.match(t -> t
                                    .field("title")
                                    .query(req.search)
                            );
                        }
                    });

            builder = builder.sort(o -> o.field(f -> f.field("created").order(SortOrder.Desc)));

            Filterer filter = Filterer.start();

            if(Util.isNotEmpty(req.category)) {
                filter.field("category.slug").is(req.category);
            }
            if(Util.isNotEmpty(req.tag)) {
                filter.field("tags.slug").is(req.tag);
            }

            return builder.from(param.getOffset())
                    .size(param.getSize())
                    .postFilter(filter.get()._toQuery()).trackTotalHits(t -> t.enabled(true));

        });

        SearchRes res = new SearchRes();
        try {
            SearchResponse<BlogArticle> response = esClient.search(request, BlogArticle.class);
            TotalHits total = response.hits().total();

            List<Hit<BlogArticle>> hits = response.hits().hits();

            List<BlogArticle> articles = new ArrayList<>();

            for (Hit<BlogArticle> hit: hits) {
                BlogArticle article = hit.source();
                articles.add(article);
            }

            res.resultBlogs = new Page<>(articles, total.value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public static SearchRes blogAggs() {
        SearchRequest request = SearchRequest.of(s -> {
            SearchRequest.Builder builder = s.index(BLOG_INDEX)
                    .query(q -> q.matchAll(t -> t));

            builder = builder.aggregations("category", a -> a.terms(h -> h.field("category.slug")));
            builder = builder.aggregations("tag", a -> a.terms(h -> h.field("tags.slug")));

            return builder;

        });

        SearchRes res = new SearchRes();
        try {
            SearchResponse<BlogArticle> response = esClient.search(request, BlogArticle.class);
            response.aggregations().forEach((field, aggregate) -> {
                res.aggregates.put(field, processAggregates(aggregate));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}
