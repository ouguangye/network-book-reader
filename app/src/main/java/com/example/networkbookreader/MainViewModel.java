package com.example.networkbookreader;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.networkbookreader.ui.home.HomeViewModel;
import com.example.networkbookreader.ui.search.SearchViewModel;

public class MainViewModel extends ViewModel {
    // View
    private MutableLiveData<View> mHomeView;
    private MutableLiveData<View> mSearchView;

    // viewModel
    private HomeViewModel homeViewModel;
    private SearchViewModel searchViewModel;

    public MainViewModel() {
        this.mHomeView = new MutableLiveData<>();
        this.mSearchView = new MutableLiveData<>();
    }

    public MutableLiveData<View> getHomeView() {
        return mHomeView;
    }

    public void setHomeView(View mHomeView) {
        this.mHomeView.setValue(mHomeView);
    }

    public MutableLiveData<View> getSearchView() {
        return mSearchView;
    }

    public void setSearchView(View mSearchView) {
        this.mSearchView.setValue(mSearchView);
    }

    public HomeViewModel getHomeViewModel() {
        return homeViewModel;
    }

    public void setHomeViewModel(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }

    public SearchViewModel getSearchViewModel() {
        return searchViewModel;
    }

    public void setSearchViewModel(SearchViewModel searchViewModel) {
        this.searchViewModel = searchViewModel;
    }
}
