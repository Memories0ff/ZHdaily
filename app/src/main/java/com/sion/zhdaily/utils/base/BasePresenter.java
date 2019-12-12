package com.sion.zhdaily.utils.base;

public abstract class BasePresenter<V extends IBaseView> {
    V view;

    public V getView() {
        return view;
    }

    public void attach(V v) {
        this.view = v;
    }

    public void detach() {
        if (view != null) {
            view = null;
        }
    }

}
