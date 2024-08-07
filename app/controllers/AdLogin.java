package controllers;

import models.Admin;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Login;
import services.DB;
import services.PasswordService;

import javax.inject.Inject;

public class AdLogin extends Controller {
	private final FormFactory formFactory;
	private final Form<Login> form;
	public static Form<Login> loginForm;

	@Inject
	public AdLogin(FormFactory formFactory) {
		this.formFactory = formFactory;
		this.form = formFactory.form(Login.class);
		loginForm = form;
	}

    public Result index(Http.Request request) {
        return ok(views.html.admin.login.render("", request));
    }

	public Result login(Http.Request request) {
		Form<Login> filledForm = loginForm.bindFromRequest(request);
		if (filledForm.hasErrors()) {
			StringBuilder err = new StringBuilder();
			filledForm.errors().forEach(error -> {
				err.append(error.message()).append("\n");
			});
			return badRequest(views.html.admin.login.render(err.toString(), request));
		} else {
			String email = filledForm.get().email.trim();
			String password = filledForm.get().password.trim();

			DB.Filter filter = DB.where();
			filter.field("email", email);
			Admin admin = DB.findOne(Admin.class,filter);


			if(admin != null && PasswordService.checkPassword(password, admin.getHashedPassword())) {
				if(!admin.isActive()) {
					return ok(views.html.admin.login.render("Account is disabled, please contact the super administrator.", request));
				}
				return redirect(routes.AdAdmin.index())
						.addingToSession(request, Auth.ADMIN, admin.getId().toString());
			} else {
				return ok(views.html.admin.login.render("Incorrect Email/Password", request));
			}
		}
	}

	public Result logout() {
		return redirect(routes.HomeController.index()).withNewSession()
				.flashing("success", "You've been logged out");
	}

	public Result startAdmin() {
		Admin admin = DB.findOne(Admin.class, DB.where().field("email","admin@carloaded.com"));
		if(admin == null) {
			admin = new Admin();
			admin.setEmail("admin@carloaded.com");
			admin.setHashedPassword("Sup3r@dm1n");
			admin.setFirstName("Admin");
			admin.setLastName("");
			DB.save(admin);
			return redirect(routes.AdLogin.index());
		} else {
			return notFound();
		}
	}
}
