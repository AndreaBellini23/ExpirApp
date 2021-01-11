package andreabellini.java.expirapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrugsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrugsFragment extends Fragment {

    private View drugsView;
    private RecyclerView drugsList;
    private DatabaseReference drugsRef;
    private FirebaseUser user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DrugsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DrugsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DrugsFragment newInstance(String param1, String param2) {
        DrugsFragment fragment = new DrugsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        drugsView = inflater.inflate(R.layout.fragment_drugs, container, false);
        drugsList = (RecyclerView) drugsView.findViewById(R.id.drugsRecyclerView);
        drugsList.setLayoutManager(new LinearLayoutManager(getContext()));

        user = FirebaseAuth.getInstance().getCurrentUser();

        drugsRef = FirebaseDatabase.getInstance().getReference().child(user.getUid()); //seleziono nodo corrispondente ad utente loggato

        //Floating Action Button per aggiungere un elemento
        FloatingActionButton fab = (FloatingActionButton) drugsView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });

        return drugsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(drugsRef, Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                String productID = getRef(position).getKey(); //Le chiavi i ogni nodo figlio
                drugsRef.child(productID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("category")) {
                            if(snapshot.child("category").getValue().toString().equalsIgnoreCase("drugs")) {
                                String name = snapshot.child("productName").getValue().toString();
                                String date = snapshot.child("expireDate").getValue().toString();


                                holder.productName.setText(name);
                                holder.expireDate.setText(date);
                            }
                            else {
                                holder.deleteProduct.setVisibility(View.GONE);
                                holder.expireDate.setVisibility(View.GONE);
                                holder.productName.setVisibility(View.GONE);
                            }

                        }

                        final DatabaseReference itemRef = getRef(position);
                        final String itemKey = itemRef.getKey();

                        holder.deleteProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drugsRef.child(itemKey).removeValue();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //mostra il layout impostato
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_display_layout, parent, false);
                ProductViewHolder viewHolder = new ProductViewHolder(view);
                return viewHolder;
            }
        };

        drugsList.setAdapter(adapter);
        adapter.startListening();
    }

    void showCustomDialog(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        final EditText productName = dialog.findViewById(R.id.productNameEditText);
        final EditText expireDate = dialog.findViewById(R.id.expireDateEditText);
        Button addProduct = dialog.findViewById(R.id.addProductButton);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProduct();                 //Leggo i valori inseriti e li carico su Firebase
                dialog.dismiss();
            }

            private void uploadProduct() {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(); //ottengo id
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(userID);

                String name = productName.getEditableText().toString();
                String date = expireDate.getEditableText().toString();
                String category = "drugs";
                Product product = new Product(name, date, category);
                ref.push().setValue(product);

            }
        });

        dialog.show();


    }
}