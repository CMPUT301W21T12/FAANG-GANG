package com.faanggang.wisetrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faanggang.wisetrack.R;
import com.faanggang.wisetrack.comment.Response;

import java.util.ArrayList;

public class ResponseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Response> responses;
    private Context context;
    public ResponseAdapter(Context context, ArrayList<Response> c) {
        this.context = context;
        this.responses = c;
    }

    @NonNull
    @Override
    public ResponseItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_response,
                parent, false);
        return new ResponseItemView(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        ResponseItemView item = (ResponseItemView) holder;
        Response rsp = responses.get(pos);
        item.getAuthorView().setText(rsp.getUsername());
        item.getDatetimeView().setText(rsp.getDateTimeString());
        item.getContentView().setText(rsp.getContent());
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }
}
