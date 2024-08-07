package controllers;

import models.BlogArticle;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.BlogSearch;
import pojos.Param;
import search.SearchRes;
import search.Searcher;
import services.DB;
import services.Exec;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogController extends Controller {

    private final FormFactory formFactory;

    @Inject
    public BlogController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result single(String slug, Http.Request request) {
        BlogArticle blogArticle = DB.findOne(BlogArticle.class, DB.where().field("slug").eq(slug));

        if(blogArticle == null) {
            return notFound();
        }

        Map<String, Object> ret = new HashMap<>();
        ret.put("blogArticle", blogArticle);

        return ok(HomeController.prettify(views.html.main.blogArticle.render(ret, request)));
    }

    public Result incViews(Long id) {
        BlogArticle blogArticle = DB.findOne(BlogArticle.class, DB.where().field("id", id));
        if(blogArticle != null) {
            blogArticle.views++;
            AdBlog.save(blogArticle);
        }
        return ok();
    }
    
    public Result posts(String categorySlug, Http.Request request) {
        Form<BlogSearch> sForm = formFactory.form(BlogSearch.class);
        Form<BlogSearch> filled = sForm.bindFromRequest(request);
        if(filled.hasErrors()) {}
        BlogSearch req = filled.get();

        if(Util.isNotEmpty(categorySlug)) {
            req.category = categorySlug;
        }

        return result(req, request);
    }

    public Result byTag(String tagSlug, Http.Request request) {
        Form<BlogSearch> sForm = formFactory.form(BlogSearch.class);
        Form<BlogSearch> filled = sForm.bindFromRequest(request);
        if(filled.hasErrors()) {}
        BlogSearch req = filled.get();

        req.tag = tagSlug;

        return result(req, request);
    }

    private Result result(BlogSearch req, Http.Request request) {
        Param param = Util.param(50, request);

        Map<String, Object> ret = new HashMap<>();
        ret.put("param", param);

        SearchRes searchRes = Searcher.blogPosts(req, param);

        ret.put("req", req);
        ret.put("res", searchRes);

        return ok(HomeController.prettify(views.html.main.blogList.render(ret, request)));
    }

    public static List<BlogArticle> popular() {
        return Searcher.blogPosts(new BlogSearch(), Param.getSome(5, "views", "desc")).resultBlogs.getList();
    }

    public static List<BlogArticle> featured() {
        return Searcher.blogPosts(new BlogSearch(), Param.getSome(2, "views", "desc")).resultBlogs.getList();
    }


}
