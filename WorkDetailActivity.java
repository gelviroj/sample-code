package com.example.mysqldemo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mysqldemo.Adapters.PartsUsedAdapter;
import com.example.mysqldemo.Adapters.TitleDataAdapter;
import com.example.mysqldemo.AddPartUsedDialog;
import com.example.mysqldemo.Entities.PartUsed;
import com.example.mysqldemo.Entities.TitleData;
import com.example.mysqldemo.Entities.User;
import com.example.mysqldemo.NewWorkDialog;
import com.example.mysqldemo.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkDetailActivity extends AppCompatActivity implements TitleDataAdapter.OnLongClickEditListener,
    PartsUsedAdapter.OnLongClickEditListener, NewWorkDialog.DialogListener, AddPartUsedDialog.DialogListener {
    RecyclerView workDetailRV;
    RecyclerView partsUsedRV;
    RecyclerView.Adapter workDetailAdapter;
    RecyclerView.Adapter partUsedAdapter;
    RecyclerView.LayoutManager workDetailLayoutManager;
    RecyclerView.LayoutManager partsUsedLayoutManager;
    TextView noPartsUsedTV;
    List<TitleData> titleDataList = new ArrayList<>();
    List<PartUsed> partUsedList= new ArrayList<>();
    List<User> userList = new ArrayList<>();
    List<String> titleStringList = new ArrayList<>();
    List<String> dataStringList = new ArrayList<>();
    List<String> newAddStringList = new ArrayList<>();
    List<String> OGdataStringList = new ArrayList<>();
    List<String> assetIdString = new ArrayList<>();
    Button addPartsBtn;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detail);
        setTitle("Work Detail ");
        workDetailRV = findViewById(R.id.workDetailRV);
        partsUsedRV = findViewById(R.id.partsUsedRV);
        addPartsBtn = findViewById(R.id.addPartsBtn);
        noPartsUsedTV = findViewById(R.id.noPartsUsedTV);
        workDetailRV.setHasFixedSize(true);
        partsUsedRV.setHasFixedSize(true);

        //Setting layout for work detail list
        workDetailLayoutManager = new LinearLayoutManager(this);
        workDetailAdapter = new TitleDataAdapter(titleDataList, WorkDetailActivity.this, this);
        workDetailRV.setAdapter(workDetailAdapter);
        workDetailRV.setLayoutManager(workDetailLayoutManager);

        //Setting layout for parts used list
        partsUsedLayoutManager = new LinearLayoutManager(this);
        partUsedAdapter = new PartsUsedAdapter(partUsedList, WorkDetailActivity.this);
        partsUsedRV.setAdapter(partUsedAdapter);
        partsUsedRV.setLayoutManager(partsUsedLayoutManager);

        addPartsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPartDialog();
            }
        });

        //Title for data to be displayed
        titleStringList.add("Date");
        titleStringList.add("Type");
        titleStringList.add("Ticket");
        titleStringList.add("Technician");
        titleStringList.add("Note");

        //Data to be displayed
        dataStringList.add("work_date");
        dataStringList.add("work_type");
        dataStringList.add("work_ticket");
        dataStringList.add("user");
        dataStringList.add("work_notes");

        //Populate default values for work detail
        Date date = new Date();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        newAddStringList.add(formattedDate);
        newAddStringList.add("Select Work Type");
        newAddStringList.add("No ticket");
        newAddStringList.add("Select a Technician");
        newAddStringList.add("");

        userList = populateTechList();
        populateWorkDetail();
        populatePartsUsedList();

        //Swipe to delete part and confirmation
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String part_used_id = viewHolder.itemView.getTag().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkDetailActivity.this);
                builder.setTitle("Delete Part Used")
                        .setMessage("Are you sure you want to delete? This cannot be undone.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String url = "https://www.justingelviro.com/delete_part.php";
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Toast.makeText(WorkDetailActivity.this,response,Toast.LENGTH_LONG).show();
                                                //workList.clear();
                                                //mAdapter.notifyDataSetChanged();
                                                populatePartsUsedList();
                                                //parseData(response);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(WorkDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("part_used_id", part_used_id);
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(WorkDetailActivity.this);
                                requestQueue.add(stringRequest);
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        populatePartsUsedList();
                    }
                });
                builder.show();
            }
        }).attachToRecyclerView(partsUsedRV);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item0 = menu.add(Menu.NONE, 0, Menu.NONE, "Home");
        item0.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item0.setIcon(R.drawable.ic_baseline_home_24);
        return super.onCreateOptionsMenu(menu);
    }

    //Commit any changes confirmation before leaving activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                boolean match = checkForMatch();
                if (match==true){
                    goHome();
                }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WorkDetailActivity.this);
                        builder.setTitle("Save changes")
                                .setMessage("Do you want to save changes made to work detail before going to home screen?\n(Click outside box to cancel.)")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                            updateWork();
                                            goHome();
                                    }
                                });
                        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goHome();
                            }
                        });
                        builder.show();
                }
                return true;
            default:
                return false;
        }
    }

    private void goHome() {
        Intent intent;
        intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void populatePartsUsedList() {
        String url = "https://www.justingelviro.com/get_parts_used.php";
        partUsedList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray dataArray = jsonObject.getJSONArray("parts_used_list");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject jsonObject1 = dataArray.getJSONObject(i);
                                String partUsedId = jsonObject1.optString("part_used_id");
                                String workId = jsonObject1.optString("work_id");
                                String partId = jsonObject1.optString("part_id");
                                String partName = jsonObject1.optString("part_name");

                                PartUsed partUsed = new PartUsed(partUsedId, workId, partId, partName);
                                partUsedList.add(partUsed);
                                noPartsUsedTV.setVisibility(View.INVISIBLE);
                            }
                            if (partUsedList.size()==0){
                                noPartsUsedTV.setVisibility(View.VISIBLE);
                            }
                            partUsedAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Bundle bundle = getIntent().getExtras();
                String work_id = bundle.getString("work_id");
                params.put("work_id", work_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private List<User> populateTechList() {
        List<User> returnUserList = new ArrayList<>();
        String url = "https://www.justingelviro.com/get_all_techs.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MaintenanceLogActivity.this,response,Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray dataArray = jsonObject.getJSONArray("tech_array");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject jsonObject1 = dataArray.getJSONObject(i);
                                String userId = jsonObject1.optString("user_id");
                                String firstName = jsonObject1.optString("first_name");
                                String lastName = jsonObject1.optString("last_name");
                                String username = jsonObject1.optString("username");

                                User user = new User(userId, firstName, lastName, username);
                                userList.add(user);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return returnUserList;
    }

    private void populateWorkDetail() {
        Bundle bundle = getIntent().getExtras();
        String workId = bundle.getString("work_id");

        if (workId.equals("new work added")){
            for (int i = 0; i < 5; i++) {
                String titleString = titleStringList.get(i);
                String dataString = newAddStringList.get(i);

                TitleData titleData = new TitleData(titleString, dataString);
                titleDataList.add(titleData);
                OGdataStringList.add(dataString);
            }
            workDetailAdapter.notifyDataSetChanged();
        }else {
            String url = "https://www.justingelviro.com/get_work_detail.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(MaintenanceLogActivity.this,response,Toast.LENGTH_LONG).show();
                            parseData(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(WorkDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    Bundle bundle = getIntent().getExtras();
                    String work_id = bundle.getString("work_id");
                    params.put("work_id", work_id);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    @Override
    public void onBackPressed() {
        boolean match = checkForMatch();
            if (match==true){
                goBack();
            }else {
                if (titleDataList.get(1).getDataData().equalsIgnoreCase("Select Work Type")){
                    Toast.makeText(WorkDetailActivity.this, "Please select a work type", Toast.LENGTH_SHORT).show(); //MUST select a work type
                }else if(titleDataList.get(3).getDataData().equalsIgnoreCase("Select a technician")){
                    Toast.makeText(WorkDetailActivity.this, "Please select a technician", Toast.LENGTH_SHORT).show(); //MUST select a technician
                } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkDetailActivity.this);
                builder.setTitle("Save changes")
                        .setMessage("Do you want to save changes made to work detail?\n(Click outside box to cancel.)")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Bundle bundle = getIntent().getExtras();
                                String asset_id = bundle.getString("asset_id");
                                String work_id = bundle.getString("work_id");
                                if (work_id.equalsIgnoreCase("new work added")){
                                    addNewWork(asset_id);
                                }else {
                                    updateWork();
                                    goBack();
                                }
                                goBack();
                            }
                        });
                builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goBack();
                    }
                });
                builder.show();
            }
        }
    }

    private void addNewWork(String asset_id_fk) {
        String url = "https://www.justingelviro.com/insert_work_v2.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(WorkDetailActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("asset_id_fk", asset_id_fk);
                params.put("work_date", titleDataList.get(0).getDataData());
                params.put("work_type", titleDataList.get(1).getDataData());
                params.put("work_ticket", titleDataList.get(2).getDataData());
                params.put("user_id_fk", getUserId());
                params.put("work_notes",  titleDataList.get(4).getDataData());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(WorkDetailActivity.this);
        requestQueue.add(stringRequest);
    }

    //When going back, pass current asset id back to maintenance log
    private void goBack() {
        Bundle bundle = getIntent().getExtras();
        String asset_id = bundle.getString("asset_id");
        String work_id = bundle.getString("work_id");

        if (work_id.equalsIgnoreCase("new work added")){
            Intent intent;
            intent = new Intent(getApplicationContext(), MaintenanceLogActivity.class);
            intent.putExtra("asset_id", asset_id);
            startActivity(intent);
        }else {
            Intent intent;
            intent = new Intent(getApplicationContext(), MaintenanceLogActivity.class);
            intent.putExtra("asset_id", assetIdString.get(0));
            startActivity(intent);
        }
    }

    private void updateWork() {
        String url = "https://www.justingelviro.com/update_work_detail.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(WorkDetailActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Bundle bundle = getIntent().getExtras();
                String work_id = bundle.getString("work_id");
                params.put("work_id", work_id);
                params.put("work_date", titleDataList.get(0).getDataData());
                params.put("work_type", titleDataList.get(1).getDataData());
                params.put("work_ticket", titleDataList.get(2).getDataData());
                params.put("user_id_fk", getUserId());
                params.put("work_notes",  titleDataList.get(4).getDataData());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String getUserId() {
        String userId = null;
        for (int i = 0; i < userList.size(); i++){
            if (titleDataList.get(3).getDataData().equals(userList.get(i).getUserName())){
                userId = userList.get(i).getUserId();
                break;
            }
        }
        return userId;
    }

    private boolean checkForMatch() {
        boolean match = true;
        for (int i = 0; i < 5; i++){
            if (!OGdataStringList.get(i).equals(titleDataList.get(i).getDataData())){
                match = false;
                break;
            }
        }
        return match;
    }

    private void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("work_detail");
            JSONObject jsonObject1 = dataArray.getJSONObject(0);
            String asset_id = jsonObject1.optString("asset_id_fk");
            assetIdString.add(asset_id);

            for (int i = 0; i < 5; i++) {
                String titleString = titleStringList.get(i);
                String dataString = jsonObject1.optString(dataStringList.get(i));

                TitleData titleData = new TitleData(titleString, dataString);
                titleDataList.add(titleData);
                OGdataStringList.add(dataString);
            }

            workDetailAdapter = new TitleDataAdapter(titleDataList, WorkDetailActivity.this, this);
            workDetailRV.setAdapter(workDetailAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //User can edit data.
    @Override
    public void onDetailClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkDetailActivity.this);
        switch (position) {
            case 0:
                Toast.makeText(this, "Please select a work date.", Toast.LENGTH_SHORT).show();
                onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                         month = month + 1;
                         String date1 =  year + "-" + month + "-" + dayOfMonth ;
                         titleDataList.get(0).setDataData(date1);
                         workDetailAdapter.notifyDataSetChanged();
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(WorkDetailActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog, onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                break;
            case 1:
                View view = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                builder.setTitle("Work Type");
                Spinner spinner = view.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(WorkDetailActivity.this, android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.workType));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                builder.setView(view);
                AlertDialog dialog1 = builder.create();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                        if (!spinner.getSelectedItem().toString().equalsIgnoreCase("Select type of work")) {
                            String newString = spinner.getSelectedItem().toString();
                            titleDataList.get(position).setDataData(newString);
                            workDetailAdapter.notifyDataSetChanged();
                            dialog1.dismiss();
                        } else {
                            Toast.makeText(WorkDetailActivity.this, "Please select a work type.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                dialog1.show();
                break;
            case 2:
                Toast.makeText(this, "Please enter the work ticket.", Toast.LENGTH_SHORT).show();
                openDialog();
                break;

            case 3:
                View view1 = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                builder.setTitle("Technician");
                Spinner spinner1 = view1.findViewById(R.id.spinner);
                ArrayAdapter<User> adapter1 = new ArrayAdapter<User>(WorkDetailActivity.this, android.R.layout.simple_spinner_item,
                        userList);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(adapter1);

                builder.setView(view1);
                AlertDialog dialog2 = builder.create();
                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                        if (!spinner1.getSelectedItem().toString().equalsIgnoreCase("Select a technician")) {
                            String newString = spinner1.getSelectedItem().toString();
                            titleDataList.get(position).setDataData(newString);
                            workDetailAdapter.notifyDataSetChanged();
                            dialog2.dismiss();
                        } else {
                            Toast.makeText(WorkDetailActivity.this, "Please select a technician.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                dialog2.show();
                break;

            case 4:
                builder.setTitle("Edit Note");
                final EditText updateInput = new EditText(WorkDetailActivity.this);
                updateInput.setSingleLine(false);
                updateInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                updateInput.setText(titleDataList.get(position).getDataData());
                builder.setView(updateInput);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newString = updateInput.getText().toString();
                        titleDataList.get(position).setDataData(newString);
                        workDetailAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //Do nothing. Return to screen
                    }
                });
                AlertDialog alert1 = builder.create();
                alert1.show();
                break;
        }
    }

    private void openDialog() {
        NewWorkDialog newWorkDialog = new NewWorkDialog();
        newWorkDialog.show(getSupportFragmentManager(), "new work dialog");
    }

    private void openAddPartDialog() {
        AddPartUsedDialog addPartUsedDialog = new AddPartUsedDialog();
        addPartUsedDialog.show(getSupportFragmentManager(), "add part dialog");
    }

    @Override
    public void applyData(String workTicket) {
        titleDataList.get(2).setDataData(workTicket);
        workDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void applyPartData(String partId) {
        insertPartUsed(partId);
    }

    private void insertPartUsed(String partId) {
        String url = "https://www.justingelviro.com/insert_part_used.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(WorkDetailActivity.this,response,Toast.LENGTH_LONG).show();
                        populatePartsUsedList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkDetailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Bundle bundle = getIntent().getExtras();
                String work_id = bundle.getString("work_id");
                params.put("work_id", work_id);
                params.put("part_id", partId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}


