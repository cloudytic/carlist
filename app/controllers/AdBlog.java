package controllers;

import authority.SecuredAdmin;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import search.Searcher;
import services.DB;
import services.S3Service;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

@Security.Authenticated(SecuredAdmin.class)
public class AdBlog extends Controller {
    private final Form<BlogArticle> blogForm;

    @Inject
    public AdBlog(FormFactory formFactory) {
        this.blogForm = formFactory.form(BlogArticle.class);
    }
    
    public Result index(Http.Request request) {
        Param param = Util.param(50, request);
        param.setSort("id");
        param.setOrder("desc");

        DB.Filter filter = DB.where();

        Optional<String> searchOp = request.queryString("search");
        if(searchOp.isPresent()) {
            String search = searchOp.get().trim();
            if(Util.isNotEmpty(search)) {
                filter.brS().field("title").like(search)
                        .or().field("content").like(search)
                        .or().field("category.name").like(search)
                        .brE();
            }
        }

        List<BlogArticle> list = DB.find(BlogArticle.class, filter, param);
        Long total = DB.count(BlogArticle.class, filter);

        Map<String, Object> ret = new HashMap<>();
        ret.put("list", list);
        ret.put("total", total);
        ret.put("param", param);

        return ok(views.html.admin.blog_articles.render(ret, request));
    }

    public Result single(Long id, Http.Request request) {
        BlogArticle blogArticle = DB.findOne(BlogArticle.class, id);
        List<BlogArticle> list = Arrays.asList(blogArticle);
        Map<String, Object> ret = new HashMap<>();
        ret.put("list", list);
        ret.put("total", 1L);
        ret.put("param", new Param(0, 1));
        return ok(views.html.admin.blog_articles.render(ret, request));
    }

    public Result add(Http.Request request) {
        BlogArticle blogArticle = new BlogArticle();
        Form<BlogArticle> formData = blogForm.fill(blogArticle);
        return ok(views.html.admin.blog_form.render(formData, blogArticle, request));
    }

    public Result create(Http.Request request) {
        Form<BlogArticle> filledForm = blogForm.bindFromRequest(request);

        BlogArticle blogArticle = new BlogArticle();

        filledForm = validateArticle(filledForm);

        if (filledForm.hasErrors()) {
            return badRequest(views.html.admin.blog_form.render(filledForm, blogArticle, request));
        } else {
            BlogArticle form = filledForm.get();
            fill(form, blogArticle);

            if(Util.isEmpty(blogArticle.slug)) {
                blogArticle.slug = Util.makeSlug(form.title);
            } else {
                blogArticle.slug = Util.makeSlug(blogArticle.slug);
            }

            blogArticle.published = true;

            blogArticle.author = Auth.getAdmin(request);

            save(blogArticle);

            return redirect(routes.AdBlog.single(blogArticle.getId()));
        }
    }

    public Result edit(Long id, Http.Request request) {
        BlogArticle blogArticle = DB.findOne(BlogArticle.class, id);
        for (BlogTag tag: blogArticle.tags) {
            blogArticle.tagList.put(tag.id.toString(), tag.id.toString());
        }
        Form<BlogArticle> filledForm = blogForm.fill(blogArticle);
        return ok(views.html.admin.blog_form.render(filledForm, blogArticle, request));
    }

    public Result update(Long id, Http.Request request) {
        BlogArticle blogArticle = DB.findOne(BlogArticle.class, id);

        Form<BlogArticle> filledForm = blogForm.bindFromRequest(request);

        filledForm = validateArticle(filledForm);
        if (filledForm.hasErrors()) {
            return badRequest(views.html.admin.blog_form.render(filledForm, blogArticle, request));
        } else {
            BlogArticle form = filledForm.get();
            fill(form, blogArticle);
            save(blogArticle);

            return redirect(routes.AdBlog.single(blogArticle.getId()));
        }
    }

    public Result delete(Long id) {
        try {
            DB.delete(BlogArticle.class, id);
        } catch (Exception e) {e.printStackTrace();}
        return ok("success");
    }

    public static void save(BlogArticle blogArticle) {
        DB.save(blogArticle);
        Searcher.indexBlogArticle(blogArticle);
    }

    public static Form<BlogArticle> validateArticle(Form<BlogArticle> filledForm) {
        Map<String, String> data = filledForm.rawData();

        String title = data.get("title");
        if (Util.isEmpty(title)) {
            filledForm = filledForm.withError("title", "This field is required");
        }

        String category = data.get("category");
        if (Util.isEmpty(category)) {
            filledForm = filledForm.withError("category", "This field is required");
        }

        String metaDescription = data.get("metaDescription");
        if (Util.isEmpty(metaDescription)) {
            filledForm = filledForm.withError("metaDescription", "This field is required");
        }


        String summary = data.get("summary");
        if (Util.isEmpty(summary)) {
            filledForm = filledForm.withError("summary", "This field is required");
        }

        String content = data.get("content");
        if (Util.isEmpty(content)) {
            filledForm = filledForm.withError("content", "This field is required");
        }

        if (!filledForm.hasErrors()) {}

        return filledForm;
    }

    public static void fill(BlogArticle form, BlogArticle blogArticle) {

        blogArticle.setCategory(form.getCategory());

        blogArticle.title = form.title;
        blogArticle.content = form.content;
        blogArticle.summary = form.summary;
        blogArticle.metaDescription = form.metaDescription;

        fillChecks(form, blogArticle);
    }

    public static void fillChecks(BlogArticle form, BlogArticle main) {
        Map<String, String> data = form.tagList;
        Set<BlogTag> tags = new HashSet<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            BlogTag tag = DB.findOne(BlogTag.class, entry.getValue());
            if (tag != null) {
                tags.add(tag);
            }
        }
        main.tags = tags;
    }

    public Result uploadImage(Http.Request request) {
        Map<String, Object> ret = new HashMap<>();

        try {
            List<String> images = new ArrayList<>();

            List<Http.MultipartFormData.FilePart<Object>> files = request.body().asMultipartFormData().getFiles();
            for (Http.MultipartFormData.FilePart<Object> filePart : files) {
                String originalName = filePart.getFilename();
                String ext = S3Service.getFileExtension(originalName);

                String fileName = Util.generateUniqueId(10)
                        + "-" + Util.makeSlug(originalName.replace("."+ext, ""))
                        + "." + ext;

                Files.TemporaryFile file = (Files.TemporaryFile) filePart.getRef();
                File original = file.path().toFile();

                S3Service.storeBlogImage(original, fileName, false);

                BlogImage image = new BlogImage();
                image.name = fileName;
                image.originalName = originalName;

                DB.save(image);

                images.add(StaticAssets.getImg("blog/"+image.name));

            }

            ret.put("images", images);

            JsonNode json = Json.toJson(ret);
            return ok(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }
}
