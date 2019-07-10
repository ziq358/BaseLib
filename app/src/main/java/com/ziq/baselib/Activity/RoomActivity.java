package com.ziq.baselib.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.recycleview.BaseViewHolder;
import com.ziq.base.recycleview.adapter.ListRecyclerAdapter;
import com.ziq.baselib.R;
import com.ziq.baselib.room.AppDataBase;
import com.ziq.baselib.room.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RoomActivity extends MvpBaseActivity {

    @BindView(R.id.rv_result)
    RecyclerView recyclerView;
    Adapter adapter;
    AppDataBase db;
    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_room;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "user.db")
                .allowMainThreadQueries()//Room不允许在主线程中访问数据库，除非在buid的时候使用allowMainThreadQueries()方法
                .build();
        adapter = new Adapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        List<User> list = db.userDao().getAll();
        adapter.setData(list);
    }

    @OnClick({R.id.btn_user_add})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_user_add:
                User user = new User();
                user.setUid(adapter.getItemCount());
                user.setFirstName("first name "+adapter.getItemCount());
                user.setLastName("last name "+adapter.getItemCount());
                db.userDao().insertAll(user);
                List<User> list = db.userDao().getAll();
                adapter.setData(list);
                break;
            default:
                break;
        }
    }


    public static class Adapter extends ListRecyclerAdapter<User> {

        public Adapter(Context context, List<User> data) {
            super(context, data);
        }

        @Override
        public int getItemLayoutRes() {
            return R.layout.item_room_user;
        }

        @Override
        public void bindDataViewHolder(BaseViewHolder holder, int position) {
            TextView textView = holder.getViewById(R.id.tv_info);
            User user = getItem(position);
            textView.setText(user.getUid() + " "+ user.getFirstName() + " "+ user.getLastName());
        }
    }

}
