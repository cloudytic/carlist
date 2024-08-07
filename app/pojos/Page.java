package pojos;

import java.util.List;

public class Page<T> {

	private long totalRowCount;
	private List<T> list;
	
	public Page(List<T> data, long total) {
		this.list = data;
		this.totalRowCount = total;
	}

	public long getTotalRowCount() {
		return totalRowCount;
	}

	public long setTotalRowCount(long totalRowCount) {
		return this.totalRowCount = totalRowCount;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> data) {
		list = data;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
	
}

