package com.example.tourdulich.trang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tourdulich.CSDL.BinhLuan;
import com.example.tourdulich.CSDL.SoSao;
import com.example.tourdulich.CSDL.Tour;
import com.example.tourdulich.CSDL.TourAdapter;
import com.example.tourdulich.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DatVe extends AppCompatActivity {

    private LinearLayout btnToiHoSo;
    private LinearLayout btnToiTrangChu;
    private LinearLayout btnToiTinTuc;
    private LinearLayout btnToiGiaoDich;

    private ListView lvTour;
    private ArrayList<Tour> arrayTour;
    private ArrayList<SoSao> arraySoSao;
    private ArrayList<BinhLuan> arrayBinhLuan;
    private TourAdapter tourAdapter;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Tour");

    private String selectedServiceType = ""; // Không lọc loại dịch vụ mặc định
    private String selectedCity = ""; // Không lọc thành phố mặc định
    private String selectedClub = ""; // Không lọc câu lạc bộ mặc định

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_ve);

        arrayTour = new ArrayList<>();
        lvTour = findViewById(R.id.listViewTour);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Chuyển Trang Thông Tin Cá Nhân
        if(firebaseUser != null) {
            //Chuyển sang TRANG THÔNG TIN CÁ NHÂN nếu đã đăng nhập thành công
            Intent ttcn = new Intent(this, ThongTinCaNhan.class);
            btnToiHoSo = findViewById(R.id.btDatVeToiHoSo);
            btnToiHoSo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ttcn);
                    overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
                }
            });
        }else {
            //Chuyển sang TRANG THÔNG TIN CÁ NHÂN nếu chưa đăng nhập
            Intent ttcncdn = new Intent(this, ThongTinChuaDangNhap.class);
            btnToiHoSo = findViewById(R.id.btDatVeToiHoSo);
            btnToiHoSo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ttcncdn);
                    overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
                }
            });
        }

        //Chuyển sang TRANG CHỦ
        Intent trangChu = new Intent(this, TrangChu.class);
        btnToiTrangChu = findViewById(R.id.btDatVeToiTrangChu);
        btnToiTrangChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(trangChu);
                overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);
            }
        });

        //Chuyển sang trang TIN TỨC
        Intent tinTuc = new Intent(this, TinTuc.class);
        btnToiTinTuc = findViewById(R.id.btDatVeToiTinTuc);
        btnToiTinTuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(tinTuc);
                overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);
            }
        });

        //Chuyển sang trang GIAO DỊCH
        Intent giaoDich = new Intent(this, GiaoDich.class);
        btnToiGiaoDich = findViewById(R.id.btDatVeToiGiaoDich);
        btnToiGiaoDich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(giaoDich);
                overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
            }
        });

        //Show toàn bộ tour trên firebase
        showTour();

        TextView filterButton = findViewById(R.id.txtBoLoc);
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void showTour(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Tour tour = snapshot.getValue(Tour.class);
                        if (tour != null) {
                            arrayTour.add(tour); // Thêm danh mục vào danh sách
                        }
                    }
                    tourAdapter = new TourAdapter(DatVe.this,arrayTour);
                    lvTour.setAdapter(tourAdapter);
//                    if(tourAdapter==null) {
//                        tourAdapter = new TourAdapter(DatVe.this, arrayTour);
//                        lvTour.setAdapter(tourAdapter);
//                    }else{
//                        tourAdapter.notifyDataSetChanged();
//                    }
                }else{
                    Toast.makeText(DatVe.this, "Không có tour nào", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DatVe.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


//        arrayTour.add(new Tour("Tour Đà Lạt",R.drawable.da_lat,"Xe","Xe",
//                "20/12/2024","21/12/2024","500.000",20));
//        arrayTour.add(new Tour("Tour Phú Quốc",R.drawable.phu_quoc,"Thuyền","Thuyền",
//                "18/12/2024","20/12/2024","1.000.000",20));
//        arrayTour.add(new Tour("Tour Hà Nội",R.drawable.ha_noi,"Máy bay","Máy bay",
//                "09/12/2024","14/12/2024","2.000.000",15));
//        arrayTour.add(new Tour("Tour Núi Bà Đen",R.drawable.nui_ba_den,"Núi","Xe",
//                "19/11/2024","19/11/2024","300.000",30));
//        arrayTour.add(new Tour("Tour Osaka-Kyoto",R.drawable.osaka_kyoto_phu_si,"Máy bay","Máy bay",
//                "20/11/2024","25/11/2024","5.000.000",15));



    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bộ lọc");

        View view = getLayoutInflater().inflate(R.layout.activity_bo_loc_dat_ve, null);
        builder.setView(view);

        Spinner serviceTypeSpinner = view.findViewById(R.id.service_type_spinner);
        Spinner citySpinner = view.findViewById(R.id.city_spinner);
        Spinner clubSpinner = view.findViewById(R.id.service_spinner);

        ArrayAdapter<CharSequence> serviceTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.service_types, android.R.layout.simple_spinner_item);
        serviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(serviceTypeAdapter);

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<CharSequence> clubAdapter = ArrayAdapter.createFromResource(this,
                R.array.cars, android.R.layout.simple_spinner_item);
        clubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(clubAdapter);

        Button applyButton = view.findViewById(R.id.apply_button);

        AlertDialog dialog = builder.create();  // Tạo đối tượng dialog
        applyButton.setOnClickListener(v -> {
            // Đặt biến bộ lọc từ các giá trị đã chọn trong hộp thoại
            selectedServiceType = serviceTypeSpinner.getSelectedItem().toString();
            selectedCity = citySpinner.getSelectedItem().toString();
            selectedClub = clubSpinner.getSelectedItem().toString();

            dialog.dismiss(); // Đóng hộp thoại sau khi áp dụng
        });

        dialog.show(); // Hiển thị hộp thoại
    }

}
