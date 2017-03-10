package com.skw.TVRecyclerViewAndFocus.holder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skw.TVRecyclerViewAndFocus.R;
import com.skw.TVRecyclerViewAndFocus.model.RecommendModel;
import com.skw.library.holder.RVHolder;

/**
 * @创建人 weishukai
 * @创建时间 17/1/10 下午2:55
 * @类描述 一句话说明这个类是干什么的
 */

public class Holder extends RVHolder<RecommendModel> {

    private ImageView bg;

    private TextView title;

    public Holder(View itemView, String focusViewType, boolean isFocusScaleAnim, boolean isTranslateAnim) {
        super(itemView, focusViewType, isFocusScaleAnim, isTranslateAnim);
        bg = (ImageView) itemView.findViewById(R.id.recommend_img);
        title = (TextView) itemView.findViewById(R.id.recommend_title);
    }

    @Override
    public void bindData(RecommendModel model, int position) {
        if (model != null) {
            bg.setBackgroundColor(model.imgResColor);
            title.setText(model.title);
        }
    }

    @Override
    public void onItemClick(View view) {
        if (getAdapterPosition() == 30) {
            recyclerView.smoothScrollToPosition(0);
        }
        Toast.makeText(view.getContext(), "点击：" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void destory() {

    }
}
