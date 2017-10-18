package com.example.twpruanhong.createorgtree;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoad.DataCallBack{
    private LinearLayout parent_linearLayout;
    private DataLoad mDataLoad;
    private ViewGroup mBeanRoot;
//    Context context = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        parent_linearLayout = (LinearLayout) findViewById(R.id.main_parent_linearLayout);
//        LinearLayout subLinear = new LinearLayout(context);
//        subLinear.setOrientation(LinearLayout.HORIZONTAL);
//        TextView leftTextView = new TextView(context);
//        leftTextView.setText("左边的textView");
//        leftTextView.setTextSize(18);
//        TextView rightTextView = new TextView(context);
//        rightTextView.setText("右边的textView");
//        rightTextView.setTextSize(18);
//        subLinear.addView(leftTextView);
//        subLinear.addView(rightTextView);
//        parent_linearLayout.addView(subLinear);

        mBeanRoot = (ViewGroup) findViewById(R.id.bean_root);
        startDataLoad();
    }

    public void startDataLoad() {
        closeDataLoader();
        mDataLoad = new DataLoad(this);
        mDataLoad.load();
    }

    public void closeDataLoader() {
        if(mDataLoad != null) {
            mDataLoad.close();
            mDataLoad = null;
        }
    }

    @Override
    public void onDataLoad(List<DataLoad.Bean> beans) {
        mBeanRoot.removeAllViews();
        inflateAndBind(this,getLayoutInflater(),mBeanRoot,beans);
    }

    public void inflateAndBind(final Context context, final LayoutInflater inflater, ViewGroup parent, final List<DataLoad.Bean> beans) {
        if (beans == null) {
            return;
        }
        for (final DataLoad.Bean bean:beans) {
            final View itemView = inflater.inflate(R.layout.activity_main_bean_item,parent,false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tipText = "";
                    if (bean.attr == null) {
                        tipText = "bean attr is null";
                    } else {
                        tipText = bean.attr.treeName;
                    }
                    Toast.makeText(context,"click " + tipText,Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ViewGroup childrenView = (ViewGroup) itemView.findViewById(R.id.bean_item_children);
                    if (childrenView.getVisibility() == View.VISIBLE) {
                        childrenView.setVisibility(View.GONE);
                    } else {
                        childrenView.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });
            ViewGroup beanItem = (ViewGroup) itemView.findViewById(R.id.bean_item);
            TextView beanItemCount = (TextView) beanItem.findViewById(R.id.bean_item_count);
            TextView beanItemText = (TextView) beanItem.findViewById(R.id.bean_item_text);
            beanItemCount.setText(indentTextWidth(bean.guessindent()));
            beanItemCount.append(String.valueOf(bean.children == null? "children is null" :"(" + bean.children.size() + ")"));
            beanItemText.setText(bean.attr == null? "bean attr is null" :bean.attr.treeName);

            if (!bean.isLeafAttr()) {
                ViewGroup childrenView = (ViewGroup) itemView.findViewById(R.id.bean_item_children);
                inflateAndBind(context,inflater,childrenView,bean.children);
            }
            parent.addView(itemView);
        }
    }

    public String indentTextWidth(int repeatCount) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< repeatCount; i ++ ) {
            builder.append("-");
        }
        return builder.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDataLoader();
    }
}
