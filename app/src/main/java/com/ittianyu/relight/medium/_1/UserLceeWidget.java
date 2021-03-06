package com.ittianyu.relight.medium._1;

import android.accounts.NetworkErrorException;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;

import com.ittianyu.relight.common.adapter.UserItemAdapter;
import com.ittianyu.relight.common.bean.UserBean;
import com.ittianyu.relight.common.datasource.UserDataSource;
import com.ittianyu.relight.widget.Widget;
import com.ittianyu.relight.widget.native_.BaseAndroidWidget;
import com.ittianyu.relight.widget.native_.FloatingActionButtonWidget;
import com.ittianyu.relight.widget.native_.FrameWidget;
import com.ittianyu.relight.widget.native_.RecyclerWidget;
import com.ittianyu.relight.widget.stateful.lcee.CommonEmptyWidget;
import com.ittianyu.relight.widget.stateful.lcee.CommonLoadingWidget;
import com.ittianyu.relight.widget.stateful.lcee.LceeStatus;
import com.ittianyu.relight.widget.stateful.lcee.LceeWidget;

import java.util.Collections;
import java.util.List;

public class UserLceeWidget extends LceeWidget {
    private List<UserBean> data = Collections.emptyList();
    private View.OnClickListener reload = v -> reload();

    public UserLceeWidget(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    @Override
    protected Widget renderLoading() {
        return new CommonLoadingWidget(context, lifecycle);
    }

    @Override
    protected Widget renderContent() {
        return new FrameWidget(context, lifecycle,
                renderRecycler(),
                renderFab()
        ).matchParent();
    }

    @Override
    protected Widget renderEmpty() {
        return new CommonEmptyWidget(context, lifecycle, "No data. Click to reload", reload);
    }

    @Override
    protected Widget renderError() {
        if (lastError != null)
            lastError.printStackTrace();
        return new CommonEmptyWidget(context, lifecycle, "Network error. Click to reload", reload);
    }

    @Override
    protected LceeStatus onLoadData() throws NetworkErrorException {
        data = UserDataSource.getInstance().getUsersFromRemote();
        if (data.isEmpty())
            return LceeStatus.Empty;
        return LceeStatus.Content;
    }

    private RecyclerWidget renderRecycler() {
        final UserItemAdapter userItemAdapter = new UserItemAdapter(lifecycle);
        return new RecyclerWidget<UserItemAdapter>(context, lifecycle)
                .adapter(userItemAdapter)
                .matchParent()
                .layoutManager(new LinearLayoutManager(context))
                .onUpdate(() -> userItemAdapter.setData(data));
    }

    private BaseAndroidWidget renderFab() {
        return new FloatingActionButtonWidget(context, lifecycle)
                .wrapContent()
                .layoutGravity(Gravity.END | Gravity.BOTTOM)
                .margin(16.f)
                .onClick(reload);
    }

}
