package controllers;

import authority.SecuredAdmin;
import models.Admin;
import models.AdminPermission;
import models.AdminRole;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.DB;

import javax.inject.Inject;
import java.util.*;

@Security.Authenticated(SecuredAdmin.class)
public class AdAdmin extends Controller {
    private final FormFactory formFactory;
    private final Form<Admin> adForm;

    @Inject
    public AdAdmin(FormFactory formFactory) {
        this.formFactory = formFactory;
        this.adForm = formFactory.form(Admin.class);
    }

    public Result index(Http.Request request) {
        //return redirect(routes.AdAutoCrud.index());
        return ok(views.html.admin.index.render(request));
    }

    public Result admins(Http.Request request) {
        Param param = Util.param(20, request);
        param.setSort("created");
        param.setOrder("desc");

        DB.Filter filter = DB.where();

        Optional<String> searchOp = request.queryString("search");
        if(searchOp.isPresent()) {
            String search = searchOp.get().trim();
            filter.brS().field("firstName").like(search)
                    .or().field("lastName").like(search)
                    .or().field("email").like(search)
                    .brE();
        }


        List<Admin> list = DB.find(Admin.class, filter, param);
        Long total = DB.count(Admin.class, filter);

        return ok(views.html.admin.admins.render(list, total, request));
    }

    public Result single(Long id, Http.Request request) {
        Admin admin = DB.findOne(Admin.class, id);
        List<Admin> list = Arrays.asList(admin);
        return ok(views.html.admin.admins.render(list, 1L, request));
    }

    public Result add(Http.Request request) {
        return ok(views.html.admin.adminForm.render(adForm, new Admin(), request));
    }

    public Result create(Http.Request request) {
        Form<Admin> filledForm = adForm.bindFromRequest(request);

        filledForm = validateAdmin(filledForm, new Admin());

        if (filledForm.hasErrors()) {
            return badRequest(views.html.admin.adminForm.render(filledForm, new Admin(), request));
        } else {
            Admin created = new Admin();
            fill(filledForm, created);
            DB.save(created);
            return redirect(routes.AdAdmin.single(created.getId()));
        }
    }

    public Result edit(Long id, Http.Request request) {
        Admin admin = DB.findOne(Admin.class, id);
        Form<Admin> filledForm = adForm.fill(admin);
        return ok(views.html.admin.adminForm.render(filledForm, admin, request));
    }

    public Result update(Long id, Http.Request request) {
        Admin admin = DB.findOne(Admin.class, id);
        Form<Admin> filledForm = adForm.bindFromRequest(request);
        filledForm = validateAdmin(filledForm, admin);
        if (filledForm.hasErrors()) {
            return badRequest(views.html.admin.adminForm.render(filledForm, admin, request));
        } else {
            fill(filledForm, admin);
            DB.save(admin);
            return redirect(routes.AdAdmin.single(admin.getId()));
        }
    }

    public static Form<Admin> validateAdmin(Form<Admin> filledForm, Admin admin) {

        String password = filledForm.field("password").value().orElse("");
        if(admin.getId() == null) {
            if (Util.isEmpty(password)) {
                filledForm = filledForm.withError("password", "Enter a password");
            }
        }

        if(Util.isNotEmpty(password)) {
            if (!Util.isValidPassword(password)) {
                filledForm = filledForm.withError("password", "Password is not strong enough");
            } else {
                String confirmPass = filledForm.field("confirmPass").value().orElse("");
                if (!password.equals(confirmPass)) {
                    filledForm = filledForm.withError("confirmPass", "Passwords don't match");
                }
            }
        }

        // Check if the email is valid
        if (!filledForm.hasErrors()) {
            Admin acct = DB.findOne(Admin.class, DB.where().field("email", filledForm.get().getEmail()));
            if(acct != null) {
                if(!acct.getId().equals(admin.getId()))
                    filledForm = filledForm.withError("email", "This email is already registered");
            }
        }
        return filledForm;
    }

    public static void fill(Form<Admin> filledForm, Admin admin) {
        Admin form = filledForm.get();

        if(Util.isNotEmpty(form.getFirstName())) {
            admin.setFirstName(form.getFirstName());
        }
        if(Util.isNotEmpty(form.getLastName())) {
            admin.setLastName(form.getLastName());
        }
        if(Util.isNotEmpty(form.getEmail())) {
            admin.setEmail(form.getEmail());
        }

        String password = filledForm.field("password").value().orElse("");
        if(Util.isNotEmpty(password)) {
            admin.setHashedPassword(password);
        }
    }

    public Result getRoles(Long id, Http.Request request) {
        Admin admin = DB.findOne(Admin.class, id);
        List<AdminRole> roles = DB.find(AdminRole.class, DB.where());
        return ok(views.html.admin.admin_roles_chi.render(admin, roles, request));
    }

    public Result setRoles(Long id, Http.Request request) {
        DynamicForm form = formFactory.form().bindFromRequest(request);
        Admin admin = DB.findOne(Admin.class, id);
        Set<AdminRole> roles = new HashSet<>();
        form.rawData().forEach((k,v) -> {
            if(k.startsWith("ids")) {
                AdminRole role = DB.findOne(AdminRole.class, v);
                if(role != null)
                    roles.add(role);
            }
        });
        admin.setRoles(roles);
        DB.save(admin);
        return redirect(routes.AdAdmin.single(admin.getId()));
    }

    public Result addRole(Http.Request request) {
        DynamicForm requestData = formFactory.form().bindFromRequest(request);
        String data = requestData.get("data");

        String message = "";
        if (Util.isNotEmpty(data)) {
            data = data.trim();
            AdminRole role = DB.findOne(AdminRole.class, DB.where().field("role", data));
            if(role != null) {
                message = "Role " + data + " already exists";
            } else {
                role = new AdminRole(data);
                DB.save(role);
            }
        }
        return redirect(routes.AdAdmin.admins() + "?show=roles")
                .flashing("message", message);
    }

    public Result editRole(Http.Request request) {
        DynamicForm requestData = formFactory.form().bindFromRequest(request);
        String data = requestData.get("data");
        String id = requestData.get("id");

        if (Util.isNotEmpty(data)) {
            data = data.trim();
            AdminRole role = DB.findOne(AdminRole.class, id);
            role.setRole(data);
            DB.save(role);
        }
        return redirect(routes.AdAdmin.admins() + "?show=roles");
    }

    public Result getPerms(Long id, Http.Request request) {
        AdminRole role = DB.findOne(AdminRole.class, id);
        return ok(views.html.admin.admin_perms_chi.render(role, request));
    }

    public Result setPerms(Long id, Http.Request request) {
        DynamicForm form = this.formFactory.form().bindFromRequest(request);
        AdminRole role = DB.findOne(AdminRole.class, id);
        List<AdminPermission> permissions = new ArrayList<>();
        form.rawData().forEach((k,v) -> {
            if(k.startsWith("perms")) {
                AdminPermission perm = AdminPermission.valueOf(v);
                permissions.add(perm);
            }
        });
        role.setPermissions(permissions);
        DB.save(role);
        return redirect(routes.AdAdmin.admins() + "?show=roles");
    }

    public Result activate(Long id) {
        Admin admin = DB.findOne(Admin.class, id);
        admin.setActive(!admin.isActive());
        DB.save(admin);
        return ok("success");
    }

    public Result delete(Long id) {
        try {
            DB.delete(Admin.class, id);
        } catch (Exception e) {e.printStackTrace();}
        return ok("success");
    }
}
