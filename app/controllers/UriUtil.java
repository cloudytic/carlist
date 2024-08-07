package controllers;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriUtil {
    public static String filterPath(String uri, String key) {
        String path = removePath(uri, key);
        if(path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static boolean contains(String text, String match) {
        if(text != null) {
            String[] arr = text.split(",");
            return Arrays.asList(arr).contains(match);
        } else{
            return false;
        }
    }

    public static String computeQuery(String uri, String key, String value) {
        uri =  uri.replaceAll(key+"=[0-9a-zA-Z_\\-.,]*", "");
        if(uri.contains("?")) {
            uri = uri + "&" + key + "=" + value;
        } else {
            uri = uri + "?" + key + "=" + value;
        }
        return uri.replace("??", "?")
                .replace("&&", "&")
                .replace("?&", "?");
    }

    public static String removeQuery(String uri, String key) {
        return (uri.replaceAll(key+"=[0-9a-zA-Z_\\-\\.,]+", "")).replaceAll("&+", "&");
    }

    public static String removePath(String uri, String key) {
        return (uri.replaceAll(key+":[0-9a-zA-Z_\\-\\.,]+", "")).replaceAll("\\/+", "/");
    }

    public static String computePath(String uri, String key, String value) {
        return (uri.replaceAll(key+":[0-9a-zA-Z_\\-\\.,]+", "") + "/" + key + ":" + value).replaceAll("\\/+", "/");
    }

    public static String getPagedUrl(String uri, int index) {
        String url = uri.replaceAll("\\&page=\\d*", "").replaceAll("page=\\d*", "");
        if(index > 0) {
            if (url.contains("?")) {
                url = url + "&page=" + index;
            } else {
                url = url + "?page=" + index;
            }
        }
        url = url.replaceFirst("\\?\\&", "?");
        if(url.endsWith("?")) url = url.substring(0, url.length() - 1);
        return url;
    }

    public static String getNoLimit(String uri) {
        String url = uri.replaceAll("\\&limit=\\d*", "").replaceAll("limit=\\d*", "");
        if(url.endsWith("?")) url = url.substring(0, url.length() - 1);
        return url;
    }

    public static String getNoPage0(String uri) {
        String url = uri.replaceAll("\\&page=0", "").replaceAll("page=0", "");
        if(url.endsWith("?")) url = url.substring(0, url.length() - 1);
        return url;
    }


    public static String getLimitUrl(String uri, int limit) {
        String url = uri.replaceAll("\\&limit=\\d*", "").replaceAll("limit=\\d*", "");
        if(url.contains("?")) {
            url = url + "&limit=" + limit;
        } else {
            url = url + "?limit=" + limit;
        }
        return url.replaceFirst("\\?\\&", "?");
    }

    public static String getSortUrl(String uri, String sort) {
        String url = uri
                .replaceAll("\\&sort=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "").replaceAll("sort=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "")
                .replaceAll("\\&order=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "").replaceAll("order=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "");
        if(url.contains("?")) {
            url = url + "&sort=" + sort;
        } else {
            url = url + "?sort=" + sort;
        }
        return url.replaceFirst("\\?\\&", "?");
    }

    public static String getNoSortUrl(String uri) {
        return uri
                .replaceAll("\\&sort=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "").replaceAll("sort=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "")
                .replaceAll("\\&order=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "").replaceAll("order=[a-zA-Z_\\-\\.,]+(.+(DESC|ASC))*", "");
    }

    public static String removeSlashes(String path) {
        if(Util.isNotEmpty(path)) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    public static String getPath(String originalPath, String type, String value) {

        originalPath = originalPath.replaceFirst("/cars", "");

        String path = "/cars";

        String mPath;
        String lPath = "";

        if (originalPath.contains("/in/")) {
            String[] arr = originalPath.split("/in/");
            mPath = removeSlashes(arr[0]);
            lPath = removeSlashes(arr[1]);
        } else {
            mPath = removeSlashes(originalPath);
        }

        if ("l".equals(type)) {
            if(Util.isNotEmpty(mPath)) {
                path += "/" + mPath;
            }
            path += "/in/" + value;
        } else if ("m".equals(type)) {
            path += "/" + value;
            if(Util.isNotEmpty(lPath)) {
                path += "/in/" + lPath;
            }
        }

        path = path.replace("//", "/");
        if(path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if(path.endsWith("/in")) {
            path = path.substring(0, path.length() - 3);
        }

        return   path;
    }
}


