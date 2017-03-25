package at.shockbytes.corey.common.core.util.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Martin Macheiner
 *         Date: 15.12.2015.
 */
public class RecyclerViewWithEmptyView extends RecyclerView{

    private View emptyView;

    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {

            Adapter<?> adapter = getAdapter();
            if(adapter != null && emptyView != null){

                //Show empty view
                if(adapter.getItemCount() == 0 && emptyView.getAlpha() == 0){
                    emptyView.animate().alpha(1).start();
                    setNestedScrollingEnabled(false);
                }
                //Hide empty view
                else if(adapter.getItemCount() != 0 && emptyView.getAlpha() == 1){
                    emptyView.animate().alpha(0).start();
                    setNestedScrollingEnabled(true);
                }
            }
        }
    };

    public RecyclerViewWithEmptyView(Context context) {
        super(context);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null){
            adapter.registerAdapterDataObserver(emptyObserver);
        }
        emptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView){
        this.emptyView = emptyView;
    }


}
