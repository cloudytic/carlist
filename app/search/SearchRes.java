package search;

import lombok.Getter;
import lombok.Setter;
import models.Account;
import models.Auto;
import models.BlogArticle;
import pojos.Page;
import pojos.SellerContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchRes {
	@Getter @Setter public Page<Auto> results;
	@Getter @Setter public Page<Account> resultsAccounts;
	@Getter @Setter public Page<SellerContact> resultSellerContacts;
	@Getter @Setter public Page<BlogArticle> resultBlogs;
	@Getter @Setter public Map<String, List<Agg>> aggregates = new HashMap<>();
	public boolean isEmpty() {
		return results.isEmpty();
	}

	public static class Agg {
		@Getter @Setter public String term;
		@Getter @Setter public long count;
		@Getter @Setter public List<Agg> children = new ArrayList<>();
	}
}
