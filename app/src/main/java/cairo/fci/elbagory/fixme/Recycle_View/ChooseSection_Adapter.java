package cairo.fci.elbagory.fixme.Recycle_View;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cairo.fci.elbagory.fixme.R;
import cairo.fci.elbagory.fixme.Workers_Section;

import java.util.ArrayList;
import java.util.List;

public class ChooseSection_Adapter extends RecyclerView.Adapter<ChooseSection_Adapter.SectionHolder> {

    List<String> list;
    Context context;




    public ChooseSection_Adapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public SectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_section, parent, false);
        return new SectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionHolder holder, final int position) {
        String mylist = list.get(position);
        holder.textView.setText(mylist);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Workers_Section.class);
                intent.putExtra("Workers_Section", list.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class SectionHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CardView cardView;


        public SectionHolder(View itemView) {
            super(itemView);
            textView= itemView.findViewById(R.id.text_view);
            cardView =  itemView.findViewById(R.id._id);




        }


        }



    public void search(List<String> newlist){
        list = new ArrayList<>();
        list.addAll(newlist);
        notifyDataSetChanged();
    }
}
