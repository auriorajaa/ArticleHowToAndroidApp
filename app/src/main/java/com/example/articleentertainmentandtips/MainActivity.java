package com.example.articleentertainmentandtips;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.articleentertainmentandtips.adapter.ArticleAdapter;
import com.example.articleentertainmentandtips.model.Article;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    private DatabaseReference articlesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        articlesRef = database.getReference("Articles");

        // Inisialisasi artikel list
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(articleList);
        recyclerView.setAdapter(articleAdapter);

        // Mendengarkan perubahan data di Firebase
        articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                articleList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Ambil UID
                    String UID = postSnapshot.getKey();
                    Article article = postSnapshot.getValue(Article.class);
                    if (article != null) {
                        article.setUID(UID);
                        articleList.add(article);
                    }
                }
                Collections.reverse(articleList);
                articleAdapter.updateArticles(articleList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log atau tangani error
            }
        });

        articleAdapter.setOnArticleClickListener(new ArticleAdapter.OnArticleClickListener() {
            @Override
            public void onArticleClick(Article article) {
                // Navigasi ke halaman detail artikel
                Intent intent = new Intent(MainActivity.this, ArticleDetailActivity.class);
                intent.putExtra("articleId", article.getUID()); // Mengirim UID artikel ke Activity detail
                startActivity(intent);
            }
        });


        // Setup bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                finish();
                return true;
            } else {
                return false;
            }
        });
    }
}
