package ken.tenunikatntt.Interface;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Emilken18 on 11/27/2018.
 */

public interface RecyclerItemTouchHelperListener {
    void onSwiped (RecyclerView.ViewHolder viewHolder, int direction, int position);
}
