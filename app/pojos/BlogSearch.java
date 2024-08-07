package pojos;

import lombok.Data;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

@Data
public class BlogSearch implements Serializable {
    public String search;

    public String category;

    public String tag;

    public BlogSearch copy() {
        return SerializationUtils.clone(this);
    }

}
