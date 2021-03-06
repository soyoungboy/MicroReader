package name.caiyao.microreader.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.itHome.ItHomeItem;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.ItHomeActivity;
import name.caiyao.microreader.utils.DBUtils;
import name.caiyao.microreader.utils.ImageLoader;

/**
 * Created by 蔡小木 on 2016/4/29 0029.
 */
public class ItAdapter extends RecyclerView.Adapter<ItAdapter.ItViewHolder> {

    private ArrayList<ItHomeItem> itHomeItems;
    private Context mContext;

    public ItAdapter(Context context, ArrayList<ItHomeItem> itHomeItems) {
        this.itHomeItems = itHomeItems;
        this.mContext = context;
    }

    @Override
    public ItViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ithome_item, parent, false);
        view.findViewById(R.id.it_card).getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                return false;
            }
        });
        return new ItViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ItViewHolder holder, int position) {
        final ItHomeItem itHomeItem = itHomeItems.get(holder.getAdapterPosition());
        if (DBUtils.getDB(mContext).isRead(Config.IT, itHomeItem.getNewsid(), 1))
            holder.tvTitle.setTextColor(Color.GRAY);
        else
            holder.tvTitle.setTextColor(Color.BLACK);
        holder.tvTitle.setText(itHomeItem.getTitle());
        holder.tvTime.setText(itHomeItem.getPostdate());
        holder.tvDescription.setText(itHomeItem.getDescription());
        ImageLoader.loadImage(mContext, itHomeItem.getImage(), holder.ivIthome);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBUtils.getDB(mContext).insertHasRead(Config.IT, itHomeItem.getNewsid(), 1);
                holder.tvTitle.setTextColor(Color.GRAY);
                mContext.startActivity(new Intent(mContext, ItHomeActivity.class)
                        .putExtra("item", itHomeItem));
            }
        });
        holder.btnIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.btnIt);
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.getMenu().removeItem(R.id.pop_fav);
                final boolean isRead = DBUtils.getDB(mContext).isRead(Config.IT, itHomeItem.getNewsid(), 1);
                if (!isRead)
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_read);
                else
                    popupMenu.getMenu().findItem(R.id.pop_unread).setTitle(R.string.common_set_unread);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.pop_unread:
                                if (isRead) {
                                    DBUtils.getDB(mContext).insertHasRead(Config.IT, itHomeItem.getNewsid(), 0);
                                    holder.tvTitle.setTextColor(Color.BLACK);
                                } else {
                                    DBUtils.getDB(mContext).insertHasRead(Config.IT, itHomeItem.getNewsid(), 1);
                                    holder.tvTitle.setTextColor(Color.GRAY);
                                }
                                break;
                            case R.id.pop_share:
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, itHomeItem.getTitle() + " http://ithome.com" + itHomeItem.getUrl() + mContext.getString(R.string.share_tail));
                                shareIntent.setType("text/plain");
                                //设置分享列表的标题，并且每次都显示分享列表
                                mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.share)));
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itHomeItems.size();
    }

    class ItViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_ithome)
        ImageView ivIthome;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.btn_it)
        Button btnIt;
        @BindView(R.id.it_card)
        CardView mCardView;

        ItViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
