package bupt.freeshare.swipelayoutlibrary;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by RuiDu on 2015/12/20.
 */
public class SwipeLayout extends FrameLayout {

    /**
     * the class which help to implement drag the item
     */
    private ViewDragHelper mViewDragHelper;

    /**
     * the child view which can be draged
     */
    private ViewGroup childView;


    private OnSwipeListener mOnSwipeListener;

    /**
     * the current state
     */
    private State mState = State.Close;

    private String TAG = "SwipeLayout";

    /**
     * the length of overlap part in px,
     * since we assumed the two layouts are one covered by another,this length may be
     * the length of the underlying layout
     */
    private int  overlapLength = 240;

    /**
     * the margin to be thought as open in px,
     * it allows an offset to the position at OPEN state
     * when it acts from CLOSE to OPEN,this value allows user drag more over the position of OPEN and slide back to OPEN
     * once released.
     * when it acts from OPEN to Close,this value allows user's slight drag be thought as cancel action
     */
    private int  openMargin = 20;

    /**
     * the margin to be thought as close in px
     * when it acts from CLOSE to OPEN,this value allows user's slight drag be thought as cancel action
     */
    private int  closeMargin = 20;

    /**
     * the margin to be thought as cancel delete in px
     * when it acts from CLOSE to OPEN,this value allows user's slight drag be thought as cancel action
     */
    private int  deleteMargin = 20;



    public enum State{
        /**
         * when the state is OPEN,user sliding the child to the right side would be thought as a close action
         */
        Open,

        /**
         *  when the state is CLOSE,user sliding the child to the right side would be thought as a delete action
         */
        Close
    }

    /**
     * an interface to do some specific works when the child view is released and slide
     * these method will be called before the slide animations.
     */
    public interface OnSwipeListener{
        public void onDelete();
        public void onOpen();
        public void onClose();
    }

    /**
     * set the SwipeListener
     * @param onSwipeListener
     */
    public void setOnOnSwipeListener(OnSwipeListener onSwipeListener){
        mOnSwipeListener = onSwipeListener;
    }

    /**
     * get the current state
     * @return
     */
    public State getState() {
        return mState;
    }

    public SwipeLayout(Context context) {
        this(context,null);

    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }


    /**
     * initialize the class,its main work is to get an instantce of ViewDragHelper
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        mViewDragHelper =ViewDragHelper.create(this, 1.0f, new SwipeCallback() );

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * we should put the event to ViewDragHelper and we need implement the call back method
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * get the second view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childView = (ViewGroup) this.getChildAt(1);
    }


    /**
     * execute the smoothly slide animation and change the state
     */
    public void close(){
        mState = State.Close;
        if(mOnSwipeListener!=null){
            mOnSwipeListener.onClose();
        }
        if(mViewDragHelper.smoothSlideViewTo(childView, 0, 0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * execute the smoothly slide animation and change the state
     */
    public void open(){
        mState = State.Open;
        if(mOnSwipeListener!=null){
            mOnSwipeListener.onOpen();
        }
        if(mViewDragHelper.smoothSlideViewTo(childView, -overlapLength, 0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * if  mOnSwipeListener is not set,we just call close() method
     */
    public void delete(){

        if(mOnSwipeListener!=null){
            mOnSwipeListener.onDelete();
        }else{
            Log.d(TAG, "delete: need an implementation!");
            close();
        }

        //reset the state
        mState = State.Close;
    }

    //works with the smoothSlideViewTo method
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);

        }
    }


    /**
     * reset the view, making sure there's no problem in reusing
     */
    public void initialState(){
        close();
    }



    class SwipeCallback extends ViewDragHelper.Callback{

        /**
         * only the second view should be draged, the first(underlying)view is acted as button
         * @param child
         * @param arg1
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int arg1) {

            return child == childView;
        }


        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            int maxLeftLeght = overlapLength+openMargin;

            if(dx>0){
                if(mState.equals(mState.Open)){

                    if(left>0){
                        return 0;
                    }else{
                        return left;
                    }
                }else{
                    return left;
                }

            }else{
                if(left<-maxLeftLeght){
                    return -maxLeftLeght;
                }else{
                    return left;
                }
            }

        }


        @Override
        public int getViewHorizontalDragRange(View child) {
            return overlapLength;
        }



        @Override
        public void onViewReleased(View releasedChild, float xvel,
                                   float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            //to see if its a swipe to right action at close state
            if(releasedChild.getLeft()>0){
                //to see if its beyond the deleteMargin
                if(releasedChild.getLeft()>deleteMargin){
                    delete();
                }else{
                    close();
                }
            //if it's not a delete action,we should keep an eye on the gesture
            }else{
                //to see if its a close action
                if(releasedChild.getLeft()>-closeMargin||
                        (releasedChild.getLeft()>-(overlapLength-openMargin)&&(xvel>0))){
                    close();
                }else{
                    open();
                }
            }

        }
    }


    /**
     * to configurate some parameters
     */
    class SwipeConfig {

        /**
         * the length of overlap part in px,
         * since we assumed the two layouts are one covered by another,this length may be
         * the length of the underlying layout
         */
        private int  overlapLength = 240;

        /**
         * the margin to be thought as open in px,
         * it allows an offset to the position at OPEN state
         * when it acts from CLOSE to OPEN,this value allows user drag more over the position of OPEN and slide back to OPEN
         * once released.
         * when it acts from OPEN to Close,this value allows user's slight drag be thought as cancel action
         */
        private int  openMargin = 20;

        /**
         * the margin to be thought as close in px
         * when it acts from CLOSE to OPEN,this value allows user's slight drag be thought as cancel action
         */
        private int  closeMargin = 20;

        /**
         * the margin to be thought as cancel delete in px
         * when it acts from CLOSE to OPEN,this value allows user's slight drag be thought as cancel action
         */
        private int  deleteMargin = 20;




        /**
         * initialize the overlaplength px
         * @param overlapLength
         */
        public SwipeConfig(int overlapLength){
            this.overlapLength = overlapLength;
        }

        /**
         * initialize the overlaplength px and all the margins as the same value
         * @param overlapLength
         * @param margins
         */
        public SwipeConfig(int overlapLength,int margins){
            this.overlapLength = overlapLength;
            this.openMargin = margins;
            this.closeMargin = margins;
            this.deleteMargin = margins;
        }

        /**
         * initialize the overlaplength px and all the margins as different value
         * @param overlapLength
         * @param openMargin
         * @param closeMargin
         * @param deleteMargin
         */
        public SwipeConfig(int overlapLength,int openMargin,int closeMargin,int deleteMargin){
            this.overlapLength = overlapLength;
            this.openMargin = openMargin;
            this.closeMargin = closeMargin;
            this.deleteMargin = deleteMargin;
        }

        public int getOverlapLength() {
            return overlapLength;
        }

        public void setOverlapLength(int overlapLength) {
            this.overlapLength = overlapLength;
        }

        public int getOpenMargin() {
            return openMargin;
        }

        public void setOpenMargin(int openMargin) {
            this.openMargin = openMargin;
        }

        public int getCloseMargin() {
            return closeMargin;
        }

        public void setCloseMargin(int closeMargin) {
            this.closeMargin = closeMargin;
        }

        public int getDeleteMargin() {
            return deleteMargin;
        }

        public void setDeleteMargin(int deleteMargin) {
            this.deleteMargin = deleteMargin;
        }
    }

}
