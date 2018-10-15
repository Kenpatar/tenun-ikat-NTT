package ken.tenunikatntt.Model;

import java.util.List;

/**
 * Created by Emilken18 on 7/23/2018.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
