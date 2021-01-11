package andreabellini.java.expirapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder{

    TextView productName, expireDate;
    Button deleteProduct;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.productNameTextView);
        expireDate = itemView.findViewById(R.id.expireDateTextView);
        deleteProduct = itemView.findViewById(R.id.deleteProductButton);

    }
}