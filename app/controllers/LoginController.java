package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Account;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import pojos.Login;
import services.CustomWsClient;
import services.DB;
import services.PasswordService;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoginController extends Controller {

	private final FormFactory formFactory;
	private final Form<Login> form;
	public static Form<Login> loginForm;

	private final CustomWsClient ws;

	@Inject
	public LoginController(FormFactory formFactory, CustomWsClient ws) {
		this.formFactory = formFactory;
		this.form = formFactory.form(Login.class);
		loginForm = form;
		this.ws = ws;
	}

    public Result index(Http.Request request) {
		return ok(views.html.main.login.render("", request));
    }

	public Result login(Http.Request request) {
		Form<Login> filledForm = loginForm.bindFromRequest(request);
		return loginAccount(request, filledForm);
	}

	private Result loginAccount(Http.Request request, Form<Login> filledForm) {
		if (filledForm.hasErrors()) {
			StringBuilder err = new StringBuilder();
			filledForm.errors().forEach(error -> {
				err.append(error.message()).append("\n");
			});
			return badRequest(views.html.main.login.render(err.toString(), request));
		} else {

			Login login = filledForm.get();
			String email = login.email.trim();
			String password = login.password.trim();

			DB.Filter filter = DB.where();
			filter.field("email", email);
			Account account = DB.findOne(Account.class,filter);

			if(account != null && PasswordService.checkPassword(password, account.getHashedPassword())) {
				if (!account.isActive()) {
					return ok(views.html.main.login.render("Account is not active, please contact the administrator.", request));
				}
				return redirect(routes.AcDashboardController.index())
						.addingToSession(request, Auth.ACCOUNT, account.getId().toString());
			} else {
				return ok(views.html.main.login.render("Incorrect Email/Password", request));
			}
		}
	}

	public Result logout() {
		return redirect(routes.HomeController.index()).withNewSession()
				.flashing("success", "You've been logged out");
	}

	public Result adminAcountLogin(Http.Request request) {

		if (request.queryString(ECRD).isPresent()) {
			String e_crd = request.queryString(ECRD).get();
			String marshalled = unmarshalling(e_crd);
			String[] cred = tokenizeCredential(marshalled);
			String email = cred[0];
			String password = cred[1];

			System.out.println(email);
			System.out.println(password);

			DB.Filter filter = DB.where();
			filter.field("email", email);
			Account account = DB.findOne(Account.class,filter);

			if (account != null && password.equals(account.getHashedPassword())) {
				System.out.println("Admin Account Login: " + account.getId());
				return redirect(routes.AcDashboardController.index()+"?ACCOUNT_SESSION=" + account.getId())
						.addingToSession(request, Auth.ACCOUNT, account.getId().toString());

			} else {
				System.out.println("Admin Account Failed: /"+ account);
			}
		}

		return redirect(routes.AdAdmin.index());
	}

	public static final String ECRD = "e_crd";
	private static final String DELIMIT = "<-->";

	public static String[] tokenizeCredential(String plainText) {
		String[] decryptedCredential  = plainText.split(DELIMIT);
		if(decryptedCredential.length == 2){
			return decryptedCredential;
		}
		throw new IllegalArgumentException("Unable to tokenize credential.");
	}

	public static String unmarshalling(String marshalledText) {
		return new String( Base64.getUrlDecoder().decode(marshalledText));// return plain text
	}

	public static String marshalling(String planText){
		return Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(planText.getBytes(StandardCharsets.UTF_8));
	}

	public static String marshall(Account account) {
		return marshalling(account.getEmail() + DELIMIT + account.getHashedPassword());
	}



	//Social Login

	public Result socialLogin(Http.Request request) throws ExecutionException, InterruptedException, JsonProcessingException {
		DynamicForm form = formFactory.form().bindFromRequest(request);
		Map<String, String> map = form.rawData();

		ObjectMapper mapper = new ObjectMapper();

		if(map.containsKey("credential")) {
			String credential = map.get("credential");

			String[] chunks = credential.split("\\.");

			Base64.Decoder decoder = Base64.getUrlDecoder();

			String header = new String(decoder.decode(chunks[0]));
			String payload = new String(decoder.decode(chunks[1]));
			System.out.println("google credentials");
			System.out.println(payload);

			Map<String, String> jsonMap = mapper.readValue(payload, Map.class);

			return returnSocialResult(request, jsonMap, false);
		}

		if(map.containsKey("access_token")) {
			String access_token = map.get("access_token");

			String response = ws.request("https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + access_token)
					.get().toCompletableFuture().get().getBody();

			System.out.println("google access_token");
			System.out.println(response);

			Map<String, String> jsonMap = mapper.readValue(response, Map.class);

			return returnSocialResult(request, jsonMap, true);
		}

		if(map.containsKey("fb_login")) {
			System.out.println("facebook login");
			System.out.println(map);

			return returnSocialResult(request, map, true);
		}

		return redirect(routes.LoginController.index());
	}

	private Result returnSocialResult(Http.Request request, Map<String, String> map, boolean ajax) {
		AccountRet ret = processSocial(request, map);
		if(ajax) {
			if (ret.account == null) {
				return ok("error");
			}
			if (ret.isNew) {
				return ok("new")
						.addingToSession(request, Auth.ACCOUNT, ret.account.getId().toString());
			} else {
				return ok("existing")
						.addingToSession(request, Auth.ACCOUNT, ret.account.getId().toString());
			}
		} else {
			if(ret.account == null) {
				return redirect(routes.LoginController.index()).flashing("flashed", "Unable to login with google");
			}
			if(ret.isNew) {
				return redirect(routes.AcProfileController.edit())
						.addingToSession(request, Auth.ACCOUNT, ret.account.getId().toString());
			} else {
				return redirect(routes.AcDashboardController.index())
						.addingToSession(request, Auth.ACCOUNT, ret.account.getId().toString());
			}
		}
	}

	private AccountRet processSocial(Http.Request request, Map<String, String> jsonMap) {
		String firstName = jsonMap.getOrDefault("given_name", "");
		String lastName = jsonMap.getOrDefault("family_name", "");
		String name = jsonMap.get("name");
		String email = jsonMap.get("email");
		//String picture = jsonMap.get("picture");

		if(Util.isEmpty(email)) {
			return new AccountRet(true, null);
		}

		Account account = DB.findOne(Account.class, DB.where().field("email", email));
		if(account == null) {
			account = new Account();
			account.setEmail(email);
			account.setFirstName(firstName);
			account.setLastName(lastName);
			account.setBusinessName(name);
			account.setHashedPassword(Util.generateUniqueId(5));
			account.setActive(true);

			AdAcctCrud.saveAccount(account);
			return new AccountRet(true, account);
		} else {
			return new AccountRet(false, account);
		}
	}

	public record AccountRet(boolean isNew, Account account) {
		public boolean isNew() {
			return isNew;
		}
	}

}
