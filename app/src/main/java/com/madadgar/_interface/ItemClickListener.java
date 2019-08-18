package com.madadgar._interface;

import android.view.View;

public interface ItemClickListener<Model> {

    void onItemClicked(Model model);

    void onItemLongClicked(View view, Model model);
}
