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







    /**
     * core class to combine view and data
     */
    class MAdapter extends RecyclerView.Adapter<MAdapter.MHolder>{


        private ArrayList<String> mLists;

        /**
         * in this situation,we assume that there's only one item
         * can be at the state OPEN at one time. so when a holder is
         * opened, we get its instance. and before you do other operations,
         * the opened item should be closed.
         */
        private MHolder nowOpen = null;

        public MAdapter(ArrayList<String> mLists){
            this.mLists = mLists;
        }




        @Override
        public MHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.m_layout, parent, false);
            MHolder holder = new MHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(final MHolder holder, final int position) {

            holder.tv.setText(mLists.get(holder.getLayoutPosition()));
            holder.swipeLayout.setOnOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                @Override
                public void onDelete() {
                    mLists.remove(holder.getLayoutPosition());

                    notifyItemRemoved(holder.getLayoutPosition());
                }

                @Override
                public void onOpen() {

                    if(nowOpen!=null&&(nowOpen!=holder)){
                        nowOpen.swipeLayout.close();
                    }
                    nowOpen = holder;

                }

                @Override
                public void onClose() {

                }
            });



            holder.llout.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(nowOpen!=null){
                        nowOpen.swipeLayout.close();
                        nowOpen =null;
                    }
                    return false;
                }
            });


    }

        public ArrayList<String> getmLists(){

            return mLists;
        }



        @Override
        public int getItemCount() {
            return mLists.size();
        }

        class MHolder extends RecyclerView.ViewHolder{

            TextView tv;
            SwipeLayout swipeLayout;
            LinearLayout llout;

            public MHolder(View itemView) {
                super(itemView);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
                tv = (TextView) itemView.findViewById(R.id.text);
                llout = (LinearLayout) itemView.findViewById(R.id.ll);


            }
        }

        /**
         * Since the view is cached when is not seen,we should restore the
         * state of the view once in cach,that is,we should reset the childlayout
         * @param holder
         */
        @Override
        public void onViewDetachedFromWindow(MHolder holder) {

            //if it was once deleted,we should reset the position of its child layout
            holder.swipeLayout.initialState();
            super.onViewDetachedFromWindow(holder);
        }

    }



}
