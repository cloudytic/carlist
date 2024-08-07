package controllers;

import authority.SecuredAdmin;
import models.Hideable;
import models.ModelBase;
import models.ModelInterface;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.DB;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

@Security.Authenticated(SecuredAdmin.class)
public class AdForms extends Controller {
    private final FormFactory formFactory;

    public static final List<String> MODELS = Arrays.asList(
        "AutoCategory",
        "AutoMake",
        "AutoModel",
        "AutoColor",
        "AutoTransmission",
        "AutoCondition",
        "AutoBodyType",
        //"AutoEngineType",
        "AutoFuelType",
        "AutoDriveTrain",
        "AutoFeature",
        //"AutoType",
        "AddressState",
        "AddressLocality",
        //"AddressArea"
        "BlogCategory",
        "BlogTag"
    );

    @Inject
    public AdForms(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index(Http.Request request) throws Exception {
        Map<String, Object> map = new HashMap<>();

        String model = request.queryString("model").orElse("");

        List<?> list = new ArrayList<>();

        if(Util.isNotEmpty(model)) {
            map.put("model", model);
            Class<?> c = Class.forName("models." + model);
            try {
                Field parent = c.getField("parent");
                Class<?> f = parent.getType();
                List<?> parents = find(f, DB.where(), Param.getAll("name","asc"));
                map.put("parentModel", f.getSimpleName());
                map.put("parentList", parents);
            } catch (Exception e) {}

            list = find(c, DB.where(), Param.getAll("name","asc"));
        }

        map.put("list", list);

        return ok(views.html.admin.elementTable.render(map, request));
    }

    public Result add(Http.Request request) {
        DynamicForm requestData = this.formFactory.form().bindFromRequest(request);

        String data = requestData.get("data");

        String model = requestData.get("model");

        String message = "";

        String[] dataArr = data.split(",");
        if(dataArr.length>1) {
            for(String d: dataArr) {
                message = persist(d.trim(), request);
            }
        } else {
            message = persist(data.trim(), request);
        }

        USelect.reset();

        return redirect(routes.AdForms.index() + "?model=" + model + "&message=" + message);
    }

    public String persist(String data, Http.Request request){
        DynamicForm requestData = this.formFactory.form().bindFromRequest(request);
        String model = requestData.get("model");
        String parentId = requestData.get("parentId");
        String parentModel = requestData.get("parentModel");
        String plural = requestData.get("plural");

        String message = "";
        try {
            Class<?> c = Class.forName("models." + model);

            Object obj = findOne(c, DB.where().field("name", data));
            if(obj != null) {
                return data + " for "+ model +" already exists";
            }


            if(model.equals("AutoCategory") || model.equals("AutoType")  || model.equals("BlogCategory")) {
                Constructor<?> cons = c.getConstructor(String.class, String.class);
                obj = cons.newInstance(data, plural.trim());
            } else {
                Constructor<?> cons = c.getConstructor(String.class);
                obj = cons.newInstance(data);
            }


            if (Util.isNotEmpty(parentId)) {
                Class<?> p = Class.forName("models." + parentModel);
                Object parent = findOne(p, parentId);
                Field field = obj.getClass().getField("parent");
                field.set(obj, parent);
            }

            DB.persist(obj);
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return message;
    }

    public Result edit(Http.Request request) {
        DynamicForm requestData = this.formFactory.form().bindFromRequest(request);
        String model = requestData.get("model");
        String data = requestData.get("data");
        String id = requestData.get("id");

        String plural = requestData.get("plural");
        String slug = requestData.get("slug");

        String message = "";
        if (Util.isNotEmpty(data)) {
            try {
                Class<?> c = Class.forName("models." + model);

                Object obj = findOne(c, id);
                Field field = obj.getClass().getField("name");
                field.set(obj, data);

                if(model.equals("AutoCategory") || model.equals("AutoType")) {
                    Field field2 = obj.getClass().getField("plural");
                    field2.set(obj, plural.trim());
                }

                if(Util.isNotEmpty(slug)) {
                    Field field3 = obj.getClass().getField("slug");
                    field3.set(obj, slug.trim());
                }

                DB.merge(obj);

            } catch (Exception e) {
                e.printStackTrace();
                message = e.getMessage();
            }
        }

        USelect.reset();

        return redirect(routes.AdForms.index() + "?model=" + model + "&message=" + message);
    }

    public Result hide(Long id, String model) {
        String message;
        try {
            Class<?> c = Class.forName("models." + model);

            Object obj = findOne(c, id);
            if(obj != null) {
                Field field = obj.getClass().getField("hide");
                boolean hide = field.getBoolean(obj);
                field.set(obj, !hide);
                DB.merge(obj);

                message = "success";
            } else {
                message = "not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return ok(message);
    }

    public Result delete(Long id, String model) {
        String message;
        try {
            Class<?> c = Class.forName("models." + model);
            delete(c, id);
            message = "success";
        } catch (Exception ex) {
            message = ex.getMessage();
        }
        return ok(message);
    }

    public Result getChildren(Long id, String model, Http.Request request) throws Exception {
        Class<?> c = Class.forName("models." + model);

        Hideable obj = (Hideable)findOne(c, id);

        List<? extends Hideable> children = obj.children(true);

        List<? extends Hideable> all = obj.children(false);

        //Collections.sort(children);
        return ok(views.html.admin.elementChildren.render(model, obj, children, all, request));
    }

    public Result postChildren(Long id, String model, Http.Request request) throws Exception {
        List<String> ids = new ArrayList<>();
        DynamicForm form = this.formFactory.form().bindFromRequest(request);
        form.rawData().forEach((k,v) -> {
            if(k.startsWith("ids")) {
                ids.add(v);
            }
        });

        Class<?> p = Class.forName("models." + model);

        Object parent = findOne(p, id);

        for(String tid: ids) {
            String[] arr = tid.split("--");

            Class<?> c = Class.forName("models." + arr[1]);
            Object child = findOne(c, arr[0]);
            Field field = child.getClass().getField("parent");
            field.set(child, parent);

            DB.save((ModelBase) child);
        }

        return redirect(routes.AdForms.index() + "?model=" + model);
    }

    public Object findOne(Class<?> c, Long id) {
        Class<? extends ModelInterface> clazz = (Class<? extends ModelInterface>) c;
        return DB.findOne(clazz, id);
    }

    public Object findOne(Class<?> c, String id) {
        Class<? extends ModelInterface> clazz = (Class<? extends ModelInterface>) c;
        return DB.findOne(clazz, id);
    }

    public Object findOne(Class<?> c, DB.Filter filter) {
        Class<? extends ModelInterface> clazz = (Class<? extends ModelInterface>) c;
        return DB.findOne(clazz, filter);
    }

    public List<?> find(Class<?> c, DB.Filter filter, Param param) {
        Class<? extends ModelInterface> clazz = (Class<? extends ModelInterface>) c;
        return DB.find(clazz, DB.where(), param);
    }

    public void delete(Class<?> c, Long id) {
        Class<? extends ModelInterface> clazz = (Class<? extends ModelInterface>) c;
        DB.delete(clazz, id);
    }

    /*
    public static class Ret {
        public String model;
        public String message;
        public Ret() {}
        public Ret(String model, String message) {
            this.model = model;
            this.message = message;
        }
    }
    */
}
