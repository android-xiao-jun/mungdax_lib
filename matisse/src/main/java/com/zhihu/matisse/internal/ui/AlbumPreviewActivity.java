/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.ui;

import static com.zhihu.matisse.internal.model.SelectedItemCollection.COLLECTION_UNDEFINED;
import static com.zhihu.matisse.internal.model.SelectedItemCollection.STATE_COLLECTION_TYPE;
import static com.zhihu.matisse.internal.model.SelectedItemCollection.STATE_SELECTION;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.allo.utils.Toasts;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.AlbumMediaCollection;
import com.zhihu.matisse.internal.ui.adapter.PreviewPagerAdapter;
import com.zhihu.matisse.ui.MatisseActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AlbumPreviewActivity extends BasePreviewActivity implements
        AlbumMediaCollection.AlbumMediaCallbacks {

    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";

    private final AlbumMediaCollection mCollection = new AlbumMediaCollection();

    private boolean mIsAlreadySetPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        mCollection.onCreate(this, this);
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM);
        mCollection.load(album);

        Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (mSpec.countable) {
            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
        } else {
            mCheckView.setChecked(mSelectedCollection.isSelected(item));
        }
        updateSize(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        List<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(Item.valueOf(cursor));
        }
//        cursor.close();

        if (items.isEmpty()) {
            return;
        }

        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        if (!mIsAlreadySetPosition) {
            //onAlbumMediaLoad is called many times..
            mIsAlreadySetPosition = true;
            Item selected = getIntent().getParcelableExtra(EXTRA_ITEM);
            int selectedIndex = items.indexOf(selected);
            mPager.setCurrentItem(selectedIndex, false);
            mPreviousPos = selectedIndex;
        }
        if (mPreviousPos < 0) {
            return;
        }
        item = items.get(mPreviousPos);
    }

    @Override
    public void onAlbumMediaReset() {

    }

    private Item item;

    @Override
    public void onBackPressed() {
        if (mSelectedCollection.isEmpty()) {
            //为空的时候返回直接关闭 不要把默认选中的图片带回上一级页面
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void sendBackResult(boolean apply) {
        if (mSelectedCollection.isEmpty()) {
            Log.e("===z","===空的");
            Intent intent = new Intent();
            intent.putExtra(EXTRA_RESULT_BUNDLE, getDataWithBundle());
            intent.putExtra(EXTRA_RESULT_APPLY, apply);
            intent.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            setResult(Activity.RESULT_OK, intent);
        } else {
            super.sendBackResult(apply);
        }
    }

    @Override
    protected void sendBackResult() {
        if (mSelectedCollection.isEmpty()) {
            Log.e("===z","===空的");
            Intent intent = new Intent();
            intent.putExtra(EXTRA_RESULT_BUNDLE, getDataWithBundle());
            intent.putExtra(EXTRA_RESULT_APPLY, true);
            intent.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            intent.putExtra(MatisseActivity.EXTRA_RESULT_SELECTION_SHOW_ONLY_ONE, true);
            setResult(Activity.RESULT_OK, intent);
        } else {
            super.sendBackResult();
        }
    }

    private Bundle getDataWithBundle() {
        Set<Item> mItems = new LinkedHashSet<>();
        mItems.add(item);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        bundle.putInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        return bundle;
    }
}
