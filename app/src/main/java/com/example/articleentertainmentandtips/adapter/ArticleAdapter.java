package com.example.articleentertainmentandtips.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.articleentertainmentandtips.R;
import com.example.articleentertainmentandtips.model.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList;
    private OnArticleClickListener onArticleClickListener;

    public ArticleAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    // Setter untuk listener ketika artikel diklik
    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.onArticleClickListener = listener;
    }

//
//    // Interface untuk listener ketika artikel diklik
//    public interface OnArticleClickListener {
//        void onArticleClick(Article article);
//    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        System.out.println("Binding article: " + article.getArticleTitle() + ", UID: " + article.getUID());
        holder.titleTextView.setText(article.getArticleTitle());
        holder.categoryTextView.setText(article.getArticleCategory());
        holder.contentTextView.setText(article.getArticleContent());

        // Format createdAt
        String formattedDate = formatTimestamp(article.getCreatedAt());
        holder.dateTextView.setText(formattedDate);

        // Set UID
        holder.uidTextView.setText("UID: " + article.getUID());

        // Use Glide to load the image from URL
        Glide.with(holder.itemView.getContext())
                .load(article.getArticleImage())
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image if not loaded yet
                .into(holder.imageView);

        // Set click listener on the whole card
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onArticleClickListener != null) {
                    onArticleClickListener.onArticleClick(article);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, categoryTextView, contentTextView, dateTextView, uidTextView;
        ImageView imageView;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.articleTitle);
            categoryTextView = itemView.findViewById(R.id.articleCategory);
            contentTextView = itemView.findViewById(R.id.articleContent);
            dateTextView = itemView.findViewById(R.id.articleDate);
            uidTextView = itemView.findViewById(R.id.articleUID); // Inisialisasi uidTextView
            imageView = itemView.findViewById(R.id.articleImage);
        }
    }

    public void updateArticles(List<Article> newArticles) {
        for (Article article : newArticles) {
            System.out.println("Updating article: " + article.getArticleTitle() + ", UID: " + article.getUID());
        }
        this.articleList = new ArrayList<>(newArticles);  // Create a new list to ensure proper update
        notifyDataSetChanged();
    }

    // Method untuk mengubah format timestamp
    public String formatTimestamp(String timestamp) {
        try {
            // Format input ISO 8601
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(timestamp);

            // Format output "dd/MM/yyyy"
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            return dateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            // Jika parsing gagal, kembalikan string timestamp asli
            return timestamp;
        }
    }
}
