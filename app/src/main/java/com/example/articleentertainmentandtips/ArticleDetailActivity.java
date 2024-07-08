package com.example.articleentertainmentandtips;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.articleentertainmentandtips.model.Article;
import com.example.articleentertainmentandtips.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ArticleDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView articleImage;
    private TextView articleTitle, createdAt, articleCategory, articleContent, noCommentsText;
    private LinearLayout commentsContainer;
    private EditText commentDisplayName, commentEmail, commentText;
    private Button submitCommentButton;

    private DatabaseReference articlesRef, commentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        articleImage = findViewById(R.id.article_image);
        articleTitle = findViewById(R.id.article_title);
        createdAt = findViewById(R.id.created_at);
        articleCategory = findViewById(R.id.article_category);
        articleContent = findViewById(R.id.article_content);
        noCommentsText = findViewById(R.id.no_comments_text);
        commentsContainer = findViewById(R.id.comments_container);
        commentDisplayName = findViewById(R.id.comment_display_name);
        commentEmail = findViewById(R.id.comment_email);
        commentText = findViewById(R.id.comment_text);
        submitCommentButton = findViewById(R.id.submit_comment_button);

        // Firebase reference
        articlesRef = FirebaseDatabase.getInstance().getReference().child("Articles");

        // Retrieve article ID from intent
        String articleId = getIntent().getStringExtra("articleId");

        if (articleId != null) {
            loadArticleDetails(articleId);
            loadComments(articleId);

            submitCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitComment(articleId);
                }
            });
        } else {
            Toast.makeText(this, "Article ID is null", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadArticleDetails(String articleId) {
        articlesRef.child(articleId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Article article = snapshot.getValue(Article.class);
                    if (article != null) {
                        toolbar.setTitle(article.getArticleTitle());

                        Glide.with(ArticleDetailActivity.this)
                                .load(article.getArticleImage())
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(articleImage);

                        articleTitle.setText(article.getArticleTitle());
                        String formattedDate = formatTimestamp(article.getCreatedAt());
                        createdAt.setText("Created at: " + formattedDate);
                        articleCategory.setText(article.getArticleCategory());
                        articleContent.setText(article.getArticleContent());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadComments(String articleId) {
        commentsRef = articlesRef.child(articleId).child("Comments");
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsContainer.removeAllViews();
                if (snapshot.exists()) {
                    noCommentsText.setVisibility(View.GONE);
                    for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                        Comment comment = commentSnapshot.getValue(Comment.class);
                        if (comment != null) {
                            TextView commentView = new TextView(ArticleDetailActivity.this);
                            commentView.setText(comment.getDisplayName() + ": " + comment.getText());
                            commentView.setTextSize(16f);
                            commentView.setTextColor(getResources().getColor(R.color.black));
                            commentView.setPadding(0, 8, 0, 8);

                            commentsContainer.addView(commentView);
                        }
                    }
                } else {
                    noCommentsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void submitComment(String articleId) {
        String displayName = commentDisplayName.getText().toString().trim();
        String email = commentEmail.getText().toString().trim();
        String text = commentText.getText().toString().trim();

        if (TextUtils.isEmpty(displayName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(text)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String commentId = commentsRef.push().getKey();
        Comment comment = new Comment(displayName, email, text);

        commentsRef.child(commentId).setValue(comment);

        commentDisplayName.setText("");
        commentEmail.setText("");
        commentText.setText("");

        Toast.makeText(this, "Comment submitted", Toast.LENGTH_SHORT).show();
    }

    private String formatTimestamp(String timestamp) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(timestamp);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timestamp;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
