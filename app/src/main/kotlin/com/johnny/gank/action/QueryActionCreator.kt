package com.johnny.gank.action
/*
 * Copyright (C) 2016 Johnny Shieh Open Source Project
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

import com.johnny.gank.core.http.GankService;
import com.johnny.gank.data.response.GankData;
import com.johnny.gank.data.ui.GankNormalItem;
import com.johnny.rxflux.Action;
import com.johnny.rxflux.Dispatcher;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
class QueryActionCreator
    @Inject constructor() {

    private var hasAction = false

    var mGankService: GankService? = null
        @Inject set

    fun query(queryText: String) {
        val action = Action.type(ActionType.QUERY_GANK).build()
        if(hasAction) {
            return
        }

        hasAction = true
        mGankService!!.queryGank(queryText, DEFAULT_COUNT, DEFAULT_PAGE)
                .filter { null != it && null != it.results && 0 != it.results.size }
                .map { GankNormalItem.newGankList(it.results) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ items ->
                    hasAction = false
                    action.getData().put(Key.QUERY_RESULT, items)
                    Dispatcher.get().postAction(action)
                }, { throwable ->
                    hasAction = false
                    Dispatcher.get().postError(action, throwable)
                })
    }

    companion object {
        private const val DEFAULT_COUNT = 27
        private const val DEFAULT_PAGE = 1

    }
}