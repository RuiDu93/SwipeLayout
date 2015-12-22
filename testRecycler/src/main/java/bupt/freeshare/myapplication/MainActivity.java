package bupt.freeshare.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bupt.freeshare.swipelayoutlibrary.SwipeLayout;

/**
 * this is the sample class to use SwipeLayout in RecyclerView
 * in this sample, we made a ListView-like view called RecyclerView,
 * and we can drag&drop item when long pressed,delete item when swipe to right and
 * get more button when swipe to right.
 * ATTENTION! RecyclerView requires two child layouts and we manipulate the second one
 * which is cover the other one called button
 */
public class MainActivity extends AppCompatActivity {



    private RecyclerView recyclerView;

    /**
     * we need use it to finish drag&drop action
     */
    private ItemTouchHelper mItemTouchHelper;


    private MAdapter mAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //first we need some data
        ArrayList<String> mLists = new ArrayList<String>();
        for(int i=0;i<30;i++){
            mLists.add("this is No."+i+" item");
        }


        recyclerView = (RecyclerView) findViewById(R.id.lv);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MAdapter(mLists);
        recyclerView.setAdapter(mAdapter);


        /*
        we should simply extends the abstract class : ItemTouchHelper.Callback
        and enable the LongPressDragEnabled
        since we should refresh data when drag&drop,we should put the the data in the callback method
         */
        mItemTouchHelper = new ItemTouchHelper(new MCallBack(mAdapter,mLists));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }



}
