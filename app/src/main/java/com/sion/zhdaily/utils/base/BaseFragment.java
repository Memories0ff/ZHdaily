package com.sion.zhdaily.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment<V extends IBaseView, P extends BasePresenter> extends Fragment implements IBaseView {
    V view;
    P presenter;

    /**
     * 获取Presenter
     *
     * @return Presenter
     */
    public P getPresenter() {
        return presenter;
    }

    /**
     * 创建MVP的View
     *
     * @return MVP的View
     */
    protected abstract V createView();

    /**
     * 创建MVP的Presenter
     *
     * @return MVP的Presenter
     */
    protected abstract P createPresenter();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        view = createView();
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attach(view);
        }
    }

    @Override
    public void onDetach() {
        if (presenter != null) {
            presenter.detach();
        }
        super.onDetach();
    }


    /**
     * Fragment中的根View
     */
    private View rootView;
    private boolean isViewCreated = false;
    private boolean isCurrentVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutRes(), container, false);
        }
        initView(rootView);
        isViewCreated = true;
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
        return rootView;
    }

    /**
     * 设置根View布局文件Resource ID
     *
     * @return 根View布局文件Resource ID
     */
    protected abstract int getLayoutRes();

    /**
     * 配置根View
     *
     * @param rootView 根View
     */
    protected abstract void initView(View rootView);

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isCurrentVisible && getUserVisibleHint()) {
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isCurrentVisible && getUserVisibleHint()) {
            dispatchUserVisibleHint(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isViewCreated) {
            if (isVisibleToUser && !isCurrentVisible) {
                dispatchUserVisibleHint(true);
            } else if (!isVisibleToUser && isCurrentVisible) {
                dispatchUserVisibleHint(false);
            }
        }
    }

    public void dispatchUserVisibleHint(boolean isVisible) {
        if (isVisible) {
            onFragmentLoad();
        } else {
            onFragmentLoadStop();
        }
    }

    /**
     * 懒加载的内容
     */
    public abstract void onFragmentLoad();

    public abstract void onFragmentLoadStop();

}
