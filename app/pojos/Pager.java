package pojos;

import java.util.ArrayList;
import java.util.List;

public class Pager {
    private final int size;
    private final long totalRowCount;
    private final int page;
    private final List<Integer> list = new ArrayList<>();

    public Pager(long total, int page, int size) {
        this.totalRowCount = total;
        this.page = page;
        this.size = size;

        if(getTotalPages() > 6) {
            int current = page + 1;
            int offset = getTotalPages() - current;
            int upper = current + (Math.min(offset, 3));
            int lower = current - (Math.min(current, 2));
            if(lower==0) lower = 1;
            for(int i = lower; i <= upper; i++ ) { list.add(i);}
        } else {
            for(int i = 1; i <= getTotalPages(); i++ ) { list.add(i);}
        }
    }

    public int getTotalPages() {
        long total = ((this.totalRowCount % size == 0) ? this.totalRowCount / size : this.totalRowCount / size + 1);
        return (int) total;
    }

    public long getTotalRowCount() {
        return totalRowCount;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public boolean hasPrev() {
        return page > 0;
    }

    public boolean hasNext() {
        return getTotalPages() > (page + 1);
    }

    public List<Integer> getList() { return list; }

    public String getDisplayXtoYofZ() {
        int start = page * size + 1;
        //int end = start + Math.min(size, list.size());
        int end = page * size + size;
        if(totalRowCount < end) end = (int)totalRowCount;

        return start + " - " + end + " of <strong>" + totalRowCount + "</strong>";
    }

    public String getDisplayXtoYofZ2() {
        int start = page * size + 1;
        //int end = start + Math.min(size, list.size());
        int end = page * size + size;
        if(totalRowCount < end) end = (int)totalRowCount;
        return start + " - " + end + " of <strong>" + totalRowCount + "</strong>";
    }

    public String getCurrentUrl(String uri, int index) {
        String url = uri.replaceAll("\\&page=\\d*", "").replaceAll("page=\\d*", "")
                .replaceAll("\\&limit=\\d*", "").replaceAll("limit=\\d*", "");
        if(url.contains("?")) {
            return url + "&limit=" + this.getSize() + "&page=" + index;
        } else {
            return url + "?limit=" + this.getSize() + "&page=" + index;
        }
    }
}
