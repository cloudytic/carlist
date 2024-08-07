package utilities;

import play.data.validation.Constraints;

public class PrImport {
	@Constraints.Required
	public String urls;

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }
}