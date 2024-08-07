package pojos;

import java.io.Serializable;

public class Param implements Serializable {
	private static final long serialVersionUID = 1L;
	private int page;
	private int size;
	private String sort;
    private String order;
	private int offset = -1; // initialize with -1 in order to be able to track when it has been set explicitly

	public Param(int page, int size) {
		this.page = page;
		this.size = size;
	}

	private Param(int page, int size, String sort, String order) {
		this.page = page;
		this.size = size;
		this.sort = sort;
		this.order = order;
	}

	public static Param get(int page, int size, String sort, String order) {
		return new Param(page, size, sort, order);
	}

	public static Param getAll(String sort, String order) {
		return new Param(0, Integer.MAX_VALUE, sort, order);
	}

	public static Param getOne(String sort, String order) {
		return new Param(0, 1, sort, order);
	}

	public static Param getSome(int size, String sort, String order) {
		return new Param(0, size, sort, order);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}


    public void setSize(int size) {
		this.size = size;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

	public int getOffset(){
		if ( -1 != offset ) {
			// then it has been set explicitly, so return it
			return offset;
		}
		return this.page * this.size;
	}

	public void setOffset(int offset){
		this.offset = offset;
	}
}
