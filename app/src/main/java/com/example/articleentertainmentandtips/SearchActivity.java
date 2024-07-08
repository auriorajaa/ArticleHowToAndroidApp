package com.example.articleentertainmentandtips;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Spinner categorySpinner;
    private RecyclerView searchRecyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    private DatabaseReference articlesRef;
    private DatabaseReference categoriesRef;
    private Map<String, String> categoryUidMap; // Map to store UID to value mapping

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);

        // Setup RecyclerView
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(articleList);
        searchRecyclerView.setAdapter(articleAdapter);

        // Firebase references
        categoriesRef = FirebaseDatabase.getInstance().getReference().child("Categories");

        // Setup Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        articlesRef = database.getReference("Articles");

        // Initialize category UID mapping
        categoryUidMap = new HashMap<>();

        // Setup Category Spinner
        setupCategorySpinner();

        articlesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                articleList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String UID = postSnapshot.getKey();
                    Article article = postSnapshot.getValue(Article.class);
                    if (article != null) {
                        article.setUID(UID); // Menetapkan UID ke artikel
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
                String articleId = article.getUID();
                System.out.println("Clicked article: " + article.getArticleTitle() + ", UID: " + articleId);
                if (articleId != null && !articleId.isEmpty()) {
                    Intent intent = new Intent(SearchActivity.this, ArticleDetailActivity.class);
                    intent.putExtra("articleId", articleId);
                    startActivity(intent);
                } else {
                    Toast.makeText(SearchActivity.this, "Article ID is null or empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                return true;
            } else {
                return false;
            }
        });

        // Setup Search EditText listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                filterArticlesByTitle(editable.toString());
            }
        });
    }

    private void setupCategorySpinner() {
        // Fetch categories from Firebase
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> categories = new ArrayList<>();
                categories.add("All Categories");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String categoryUid = dataSnapshot.getKey(); // Get UID
                    String categoryValue = dataSnapshot.getValue(String.class); // Get value
                    categories.add(categoryValue); // Add value (category name) to the list
                    categoryUidMap.put(categoryUid, categoryValue); // Map UID to value
                }

                // Setup Spinner with adapter
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_item, categories);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(spinnerAdapter);

                // Spinner item selected listener for filtering by category
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String selectedCategoryValue = (String) adapterView.getItemAtPosition(position);
                        if (!selectedCategoryValue.equals("All Categories")) {
                            String selectedCategoryUid = getKeyFromValue(categoryUidMap, selectedCategoryValue);
                            filterArticlesByCategory(selectedCategoryUid);
                        } else {
                            filterArticlesByCategory("All Categories");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    // Helper method to get key from value in categoryUidMap
    private String getKeyFromValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void filterArticlesByTitle(String query) {
        String lowerCaseQuery = query.toLowerCase();
        List<Article> filteredList = new ArrayList<>();
        for (Article article : articleList) {
            if (article.getArticleTitle().toLowerCase().contains(lowerCaseQuery)) {
                System.out.println("Filtered article: " + article.getArticleTitle() + ", UID: " + article.getUID());
                filteredList.add(article);
            }
        }
        articleAdapter.updateArticles(filteredList);
    }

    private void filterArticlesByCategory(String categoryUid) {
        // Filter articles by category UID
        if (categoryUid.equals("All Categories")) {
            articlesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    articleList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String UID = dataSnapshot.getKey(); // Ambil UID dari snapshot
                        Article article = dataSnapshot.getValue(Article.class);
                        if (article != null) {
                            article.setUID(UID); // Tetapkan UID ke artikel
                            articleList.add(article);
                        }
                    }
                    Collections.reverse(articleList);
                    articleAdapter.updateArticles(articleList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        } else {
            String categoryId = categoryUidMap.get(categoryUid); // Get category value from UID
            Query articleQuery = articlesRef.orderByChild("ArticleCategory").equalTo(categoryId);
            articleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    articleList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String UID = dataSnapshot.getKey(); // Ambil UID dari snapshot
                        Article article = dataSnapshot.getValue(Article.class);
                        if (article != null) {
                            article.setUID(UID); // Tetapkan UID ke artikel
                            articleList.add(article);
                        }
                    }
                    Collections.reverse(articleList);
                    articleAdapter.updateArticles(articleList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

}
