package controllers;

import models.Account;
import models.Admin;
import models.AdminPermission;
import play.mvc.Http;
import services.DB;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Auth {

    public static final String ACCOUNT = "account";

    public static boolean isAccount(Http.Request request) {
        return getAccount(request) != null;
    }

    public static Account getAccount(Http.Request request) {
        String id = request.session().get(ACCOUNT).orElse("");
        if(Util.isNotEmpty(id)) {
            return DB.findOne(Account.class, id);
        }
        return null;
    }

    public static final String ADMIN = "admin";

    public static Admin getAdmin(Http.Request request) {
        Optional<String> idOption = request.session().get(ADMIN);
        if(idOption.isPresent()) {
            String id = idOption.get();
            return DB.findOne(Admin.class, id);
        }
        return null;
    }

    public static boolean isAdmin(Http.Request request) {
        return getAdmin(request) != null;
    }

    public static boolean hasPerm(AdminPermission perm, Http.Request request) {
        Admin admin = getAdmin(request);
        if(admin != null) {
            Set<AdminPermission> permissions = admin.permissions();
            if(permissions.contains(AdminPermission.ALL) || permissions.contains(AdminPermission.SUPER)) {
                return true;
            }
            return permissions.contains(perm);
        }
        return false;
    }

    public static boolean hasJustPerm(AdminPermission perm, Http.Request request) {
        Admin admin = getAdmin(request);
        if(admin != null) {
            Set<AdminPermission> permissions = admin.permissions();
            return permissions.contains(perm);
        }
        return false;
    }

    public static boolean hasAnyPerm(Http.Request request, AdminPermission... perms) {
        Admin admin = getAdmin(request);
        if(admin != null) {
            Set<AdminPermission> permissions = admin.permissions();
            if(permissions.contains(AdminPermission.ALL)) {
                return true;
            }
            for (AdminPermission perm : perms) {
                if (permissions.contains(perm)) {
                    return true;
                }
            }
        }
        return false;
    }
}
