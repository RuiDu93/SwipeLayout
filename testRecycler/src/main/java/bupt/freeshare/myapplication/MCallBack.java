package bupt.freeshare.myapplication;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by RuiDu on 2015/12/18.
 */
public class MCallBack extends ItemTouchHelper.Callback {


    MainActivity.MAdapter mAdapter;
    ArrayList<String> mLists;
    public MCallBack(MainActivity.MAdapter mAdapter, ArrayList<String> mLists){
        this.mLists = mLists;
        this.mAdapter = mAdapter;
    }

    /**
     * since its swipe to miss method conflicts with dealing events in
     * SwipeLayout,we should disabled this function
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * we can do nothing
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.DOWN|ItemTouchHelper.UP,0);
    }

    /**
     * when the items are swaped,we should switch the item in data set meanwhile
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Collections.swap(mLists,viewHolder.getAdapterPosition(),target.getAdapterPosition());
        mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }


}
