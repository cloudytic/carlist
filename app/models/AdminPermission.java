package models;

public enum AdminPermission {
	SUPER,
	ALL,
	MANAGE_ADMINS,
	MANAGE_SETTINGS,
	MANAGE_ACCOUNTS,
	MANAGE_INVENTORY,
	MANAGE_BLOG;
	
	public String id;
	
	AdminPermission(){
		this.id = name();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
}
