package hu.gyulavari.adam.caloclient;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import hu.gyulavari.adam.caloclient.manager.ApiManager;
import hu.gyulavari.adam.caloclient.misc.EntryAdapter;
import hu.gyulavari.adam.caloclient.misc.PickDate;
import hu.gyulavari.adam.caloclient.misc.PickTime;
import hu.gyulavari.adam.caloclient.misc.Utils;
import hu.gyulavari.adam.caloclient.model.Entry;
import hu.gyulavari.adam.caloclient.model.Filter;
import hu.gyulavari.adam.caloclient.rest.EntryResponse;
import hu.gyulavari.adam.caloclient.rest.UserResponse;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String EXTRA_USER = "extra_user";

    private EntryAdapter adapter;
    private boolean showFilters, creating;
    private UserResponse user;

    private ProgressDialog progress;
    private View filterContainer;
    private Button fromDate, toDate, fromTime, toTime;
    private ProgressBar pbGoal;
    private View pending;

    private SwipeMenuListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEntryDialog(null);
            }
        });
        fab.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_plus)
                .colorRes(android.R.color.white)
                .actionBarSize());

        listView = (SwipeMenuListView) findViewById(R.id.listView);

        adapter = new EntryAdapter(this);
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showEntryDialog(adapter.getItem(position));
                        break;
                    case 1:
                        showDeleteDialog(adapter.getItem(position));
                        break;
                }
                return false;
            }
        });

        filterContainer = findViewById(R.id.filter_container);
        fromDate = (Button) findViewById(R.id.from_date);
        fromDate.setOnClickListener(this);
        toDate = (Button) findViewById(R.id.to_date);
        toDate.setOnClickListener(this);
        fromTime = (Button) findViewById(R.id.from_time);
        fromTime.setOnClickListener(this);
        toTime = (Button) findViewById(R.id.to_time);
        toTime.setOnClickListener(this);

        pbGoal = (ProgressBar) findViewById(R.id.progressBar);

        if (getIntent().hasExtra(EXTRA_USER)) {
            user = (UserResponse) getIntent().getSerializableExtra(EXTRA_USER);
            setUpGoal();
        } else {
            showProgress(true);
            ApiManager.getInstance().getUser(userResponseCallback);
        }

        loadEntries();
    }

    private void setUpGoal() {
        if (user.goal > 0) {
            pbGoal.setMax(user.goal);
            pbGoal.setVisibility(View.VISIBLE);
        } else
            pbGoal.setVisibility(View.GONE);
        refreshGoal();
    }

    private void refreshGoal() {
        if (user == null || user.goal == 0)
            return;
        if (user.goal <= adapter.getSum()) {
            pbGoal.getProgressDrawable().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);
            pbGoal.setProgress(user.goal);
        } else {
            pbGoal.getProgressDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
            pbGoal.setProgress(adapter.getSum());
        }

    }

    private void loadEntries() {
        showProgress(true);
        ApiManager.getInstance().getEntries(new Filter(fromDate.getTag(), toDate.getTag(),
                        fromTime.getTag(), toTime.getTag()),
                new Callback<EntryResponse>() {
                    @Override
                    public void onResponse(Response<EntryResponse> response, Retrofit retrofit) {
                        adapter.clear();
                        adapter.addAll(response.body().entries);
                        refreshGoal();
                        showProgress(false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        showProgress(false);
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_filter).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_filter)
                .colorRes(android.R.color.white).actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (showFilters)
                clearFilters();
            else {
                showFilters = true;
                filterContainer.setVisibility(View.VISIBLE);
            }
            loadEntries();
        } else {
            showSettingsDialog();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        pending = v;
        DialogFragment df;
        if (v == fromDate || v == toDate || v.getId() == R.id.date)
            df = new PickDate().setData(v.getTag());
        else
            df = new PickTime().setData(v.getTag());
        df.show(getSupportFragmentManager(), "picker_dialog");
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        setPendingData(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        setPendingData(hourOfDay + ":" + minute);
    }

    private void setPendingData(String s) {
        pending.setTag(s);
        ((Button)pending).setText(s);
        pending = null;
        if (!creating)
            loadEntries();
    }

    private void showEntryDialog(final Entry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_entry, null);
        final EditText title = (EditText) view.findViewById(R.id.title);
        final EditText num = (EditText) view.findViewById(R.id.num);
        final Button date = (Button) view.findViewById(R.id.date);
        final Button time = (Button) view.findViewById(R.id.time);
        final View error = view.findViewById(R.id.error);
        if (entry != null) {
            title.setText(entry.title);
            num.setText(String.valueOf(entry.num));
            date.setText(entry.entry_date);
            date.setTag(entry.entry_date);
            time.setText(Utils.toTime(entry.entry_time));
            time.setTag(Utils.toTime(entry.entry_time));
        }
        date.setOnClickListener(this);
        time.setOnClickListener(this);
        final AlertDialog d = builder.setTitle(R.string.new_entry)
                .setView(view)
                .setPositiveButton(R.string.create, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        creating = false;
                    }
                }).create();
        d.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEntry(title, num, date, time, error)) {
                    return;
                }
                d.dismiss();
                showProgress(true);
                creating = false;
                Entry newEntry;
                if (entry == null)
                    newEntry = new Entry();
                else
                    newEntry = entry;
                newEntry.title = title.getText().toString();
                newEntry.num = Integer.valueOf(num.getText().toString());
                newEntry.entry_date = date.getText().toString();
                newEntry.entry_time = Utils.fromTime(time.getText().toString());
                if (entry == null)
                    ApiManager.getInstance().createEntry(newEntry, entryResponseCallback);
                else
                    ApiManager.getInstance().updateEntry(newEntry, entryResponseCallback);
            }
        });
        creating = true;
    }

    private boolean checkEntry(EditText title, EditText num, Button date, Button time, View error) {
        boolean result = false;
        if (TextUtils.isEmpty(title.getText().toString())) {
            title.setError(getString(R.string.error_field_required));
            result = true;
        }
        if (TextUtils.isEmpty(num.getText().toString())) {
            num.setError(getString(R.string.error_field_required));
            result = true;
        }
        if (date.getTag() == null || time.getTag() == null) {
            error.setVisibility(View.VISIBLE);
            result = true;
        }
        return result;
    }

    private void showDeleteDialog(final Entry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgress(true);
                        ApiManager.getInstance().deleteEntry(entry, entryResponseCallback);
                    }
                })
                .setNegativeButton(R.string.cancel, null).create().show();
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_settings, null);
        final EditText goal = (EditText) view.findViewById(R.id.editText);
        goal.setText(String.valueOf(user.goal));
        builder.setTitle(R.string.action_settings)
                .setView(view)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApiManager.getInstance().updateGoal(user.id, Integer.parseInt(goal.getText().toString()), userResponseCallback);
                    }
                })
                .setNegativeButton(R.string.cancel, null).create().show();
    }

    private void clearFilters() {
        showFilters = false;
        filterContainer.setVisibility(View.GONE);
        fromDate.setText(R.string.from_date);
        fromDate.setTag(null);
        toDate.setText(R.string.to_date);
        toDate.setTag(null);
        fromTime.setText(R.string.from_time);
        fromTime.setTag(null);
        toTime.setText(R.string.to_time);
        toTime.setTag(null);
    }

    private void showProgress(boolean show) {
        if (show && progress == null) {
            progress = ProgressDialog.show(this, getString(R.string.loading), "", true);
        } else if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                    0xCE)));
            openItem.setWidth(getResources().getDimensionPixelSize(R.dimen.swipe_button_size));
            openItem.setIcon(new IconDrawable(MainActivity.this, FontAwesomeIcons.fa_edit)
                    .colorRes(android.R.color.white).actionBarSize());

            menu.addMenuItem(openItem);

            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            deleteItem.setWidth(getResources().getDimensionPixelSize(R.dimen.swipe_button_size));
            deleteItem.setIcon(new IconDrawable(MainActivity.this, FontAwesomeIcons.fa_trash)
                                .colorRes(android.R.color.white).actionBarSize());

            menu.addMenuItem(deleteItem);
        }
    };

    Callback<EntryResponse> entryResponseCallback = new Callback<EntryResponse>() {
        @Override
        public void onResponse(Response<EntryResponse> response, Retrofit retrofit) {
            clearFilters();
            adapter.clear();
            adapter.addAll(response.body().entries);
            refreshGoal();
            showProgress(false);
        }

        @Override
        public void onFailure(Throwable t) {
            showProgress(false);
            t.printStackTrace();
        }
    };

    Callback<UserResponse> userResponseCallback = new Callback<UserResponse>() {
        @Override
        public void onResponse(Response<UserResponse> response, Retrofit retrofit) {
            user = response.body();
            setUpGoal();
            showProgress(false);
        }

        @Override
        public void onFailure(Throwable t) {
            showProgress(false);
            t.printStackTrace();
        }
    };
}
