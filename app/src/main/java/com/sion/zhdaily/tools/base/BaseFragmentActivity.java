package com.sion.zhdaily.tools.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseFragmentActivity<V extends IBaseView, P extends BasePresenter<V>> extends FragmentActivity {
    V view;
    P presenter;

    public P getPresenter() {
        return presenter;
    }

    protected abstract V createView();

    protected abstract P createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = createView();
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attach(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detach();
        }
    }
}
